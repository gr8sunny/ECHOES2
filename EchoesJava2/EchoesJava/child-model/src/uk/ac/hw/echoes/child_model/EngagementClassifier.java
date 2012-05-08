package uk.ac.hw.echoes.child_model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import weka.classifiers.Classifier;
import weka.classifiers.rules.JRip;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import Ice.Application;
import Ice.Current;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import IceStorm.AlreadySubscribed;
import IceStorm.BadQoS;
import IceStorm.NoSuchTopic;
import IceStorm.TopicExists;
import IceStorm.TopicManagerPrx;
import IceStorm.TopicManagerPrxHelper;
import IceStorm.TopicPrx;
import echoes.ChildModelListenerPrx;
import echoes.ChildModelListenerPrxHelper;
import echoes.Engagement;
import echoes.FacialExpression;
import echoes.ScreenRegion;
import echoes._ChildModelListenerDisp;
import echoes._PauseListenerDisp;
import echoes._RenderingListenerDisp;
import echoes._TouchListenerDisp;
import echoes._UserHeadListenerDisp;

public class EngagementClassifier {
	private Timer timer;
	private Classifier classifier;
	private TimerTask classifyTask;
	private Instances trainingData;
	
	private Map<String, Boolean> values;
	private Map<String, ScreenRegion> objectLocations;
    private static final String AGENT_ID = "AGENT";
	
	private TopicPrx userHeadTopic;
	private TopicPrx rendererTopic;
	private TopicPrx touchTopic;
	private TopicPrx pauseTopic;
	private ObjectPrx uhPrx;
	private ObjectPrx rlPrx;
	private ObjectPrx tlPrx;
	private ObjectPrx plPrx;
	
	private ChildModelListenerPrx publisher;
	
	private Engagement curEngagement;
	private String targetObjId;
	
	private boolean paused;

	public EngagementClassifier(ObjectAdapter adapter) {
		targetObjId = "";
		paused = false;
		
		// Load the data and train the classifier
		File dataDir = new File("share/child-model/");

		try {
			DataSource source = new DataSource(new FileInputStream(new File(
					dataDir, "training-data.arff")));
			trainingData = source.getDataSet();
			trainingData.setClassIndex(trainingData.numAttributes()-1);
			
			// Replace all missing values with the attribute mean
			Filter replacer = new ReplaceMissingValues();
			replacer.setInputFormat(trainingData);
			trainingData = Filter.useFilter(trainingData, replacer);
			Application.communicator().getLogger().trace("info", "Done replacement of missing values");
			
			// Re-sample to get something closer to a uniform distribution
			Resample resampler = new Resample();
			resampler.setBiasToUniformClass(0.7);
			resampler.setInputFormat(trainingData);
			trainingData = Filter.useFilter(trainingData, resampler);
			Application.communicator().getLogger().trace("info", "Done resampling");

			classifier = new JRip();
			classifier.buildClassifier(trainingData);
			Application.communicator().getLogger().trace("info", "Classifier trained: " + classifier);
		} catch (FileNotFoundException e) {
			Application.communicator().getLogger()
					.error(
							"File not found for training classifier: "
									+ e.getMessage());
		} catch (Exception e) {
			Application.communicator().getLogger().error(
					"Error training classifier: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Initialise the values
		values = Collections.synchronizedMap(new HashMap<String, Boolean>());
		resetValues();
		
		objectLocations = Collections.synchronizedMap(new HashMap<String, ScreenRegion>());
		
		// Listen on all relevant channels
		// We need to listen for messages from
		// - head tracker
		// - touch server
		// - rendering engine
		// ... others?

        String iceStormName = Ice.Application.communicator().getProperties().getProperty(
                "IceStorm.InstanceName");
        TopicManagerPrx tmPrx = TopicManagerPrxHelper.checkedCast(Ice.Application.communicator()
                .stringToProxy(iceStormName + "/TopicManager"));
        
		try {
			pauseTopic = tmPrx
					.retrieve(_PauseListenerDisp.ice_staticId());
		} catch (NoSuchTopic ex) {
			try {
				pauseTopic = tmPrx.create(_PauseListenerDisp
						.ice_staticId());
			} catch (TopicExists e) {
				Application.communicator().getLogger().warning(
						"Unable to connect to pause topic");
			}
		}
		
		_PauseListenerDisp pauseListener = new _PauseListenerDisp() {
			@Override
			public void setPaused(boolean paused, Current current) {
				EngagementClassifier.this.setPaused(paused);
			}
		};
		plPrx = adapter.addWithUUID(pauseListener);
		
        try {
            userHeadTopic = tmPrx.retrieve(_UserHeadListenerDisp.ice_staticId());
        } catch (NoSuchTopic ex) {
            try {
                userHeadTopic = tmPrx.create(_UserHeadListenerDisp.ice_staticId());
            } catch (TopicExists e) {
                Application.communicator().getLogger().warning("Unable to connect to gaze topic");
            }
        }
        
        UserHeadListenerImpl uhListener = new UserHeadListenerImpl();
        uhPrx = adapter.addWithUUID(uhListener);
        
        if (userHeadTopic != null) {
            try {
                userHeadTopic.subscribeAndGetPublisher(new HashMap<String, String>(), uhPrx);
            } catch (AlreadySubscribed e) {
                e.printStackTrace();
            } catch (BadQoS e) {
                e.printStackTrace();
            }
        }
        
        try {
            rendererTopic = tmPrx.retrieve(_RenderingListenerDisp.ice_staticId());
        } catch (NoSuchTopic ex) {
            try {
                rendererTopic = tmPrx.create(_RenderingListenerDisp.ice_staticId());
            } catch (TopicExists e) {
                Application.communicator().getLogger().warning("Unable to connect to renderer topic");
            }
        }
        
        RenderingListenerImpl rListener = new RenderingListenerImpl();
        rlPrx = adapter.addWithUUID(rListener);
        
        if (rendererTopic != null) {
            try {
                rendererTopic.subscribeAndGetPublisher(new HashMap<String, String>(), rlPrx);
            } catch (AlreadySubscribed e) {
                e.printStackTrace();
            } catch (BadQoS e) {
                e.printStackTrace();
            }
        }
        
        try {
            touchTopic = tmPrx.retrieve(_TouchListenerDisp.ice_staticId());
        } catch (NoSuchTopic ex) {
            try {
                touchTopic = tmPrx.create(_TouchListenerDisp.ice_staticId());
            } catch (TopicExists e) {
                Application.communicator().getLogger().warning("Unable to connect to touch topic");
            }
        }
        
        TouchListenerImpl tListener = new TouchListenerImpl();
        tlPrx = adapter.addWithUUID(tListener);
        
        if (touchTopic != null) {
            try {
                touchTopic.subscribeAndGetPublisher(new HashMap<String, String>(), tlPrx);
            } catch (AlreadySubscribed e) {
                e.printStackTrace();
            } catch (BadQoS e) {
                e.printStackTrace();
            }
        }
        
        TopicPrx cmTopic = null;
        try {
			cmTopic = tmPrx.retrieve(_ChildModelListenerDisp.ice_staticId());
		} catch (NoSuchTopic e) {
			try {
				cmTopic = tmPrx.create(_ChildModelListenerDisp.ice_staticId());
			} catch (TopicExists e1) {
				Application.communicator().getLogger().warning("Unable to connect to child-model topic");
			}
		}
		publisher = ChildModelListenerPrxHelper.uncheckedCast(cmTopic.getPublisher());
		
		this.timer = new Timer();
	}

	@SuppressWarnings("unchecked")
	private void resetValues() {
		for (Enumeration<Attribute> e = trainingData.enumerateAttributes(); e.hasMoreElements(); ) {
			Attribute att = e.nextElement();
			values.put(att.name(), false);
		}
	}
	
	public void start() {
		classifyTask = new TimerTask() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				
				if (paused) {
					return;
				}
				
				// Create the instance based on the current set of values
				synchronized(values) {
					Instance inst = new Instance(trainingData.instance(0).numAttributes());
					inst.setDataset(trainingData);
					for (Enumeration<Attribute> e = trainingData.enumerateAttributes(); e.hasMoreElements(); ) {
						Attribute att = e.nextElement();
						// Depending on the attribute, we set the value based on recent events
						Boolean value = values.get(att.name());
						if (value == null) {
							Application.communicator().getLogger().warning("Unknown attribute: " + att.name());
						} else {
							inst.setValue(att, value ? 1 : 0);
						}
					}
					try {
						double[] classification;
						classification = classifier.distributionForInstance(inst);
						int cls = -1;
						double probability = 0;
						for (int i = 0; i < inst.attribute(inst.numAttributes()-1).numValues(); i++) {
							if (classification[i] > probability) {
								cls = i;
								probability = classification[i];
							}
						}
						
						if (cls >= 0) {
							Engagement eng = Engagement.valueOf(inst.attribute(inst.numAttributes()-1).value(cls));
							if (eng != curEngagement) {
								Application.communicator().getLogger().trace("info", inst + " classified as " + eng + " (" + probability + ")");
								publisher.engagementEstimate(eng, probability);
								curEngagement = eng;
							}
						} else {
							Application.communicator().getLogger().warning("Couldn't determine class for " + inst);
						}
					} catch (Exception e) {
						Application.communicator().getLogger().warning("Couldn't classify instance " + inst + ": " + e.getMessage());
					}
					
					resetValues();
				}
			}
		};
		
		timer.schedule(classifyTask, 0, 1000);
	}
	
	public void stop() {
		classifyTask.cancel();
		userHeadTopic.unsubscribe(uhPrx);
		rendererTopic.unsubscribe(rlPrx);
		touchTopic.unsubscribe(tlPrx);
		pauseTopic.unsubscribe(plPrx);
	}
	
	public Engagement getEngagement() {
		return curEngagement;
	}
	
    private class UserHeadListenerImpl extends _UserHeadListenerDisp {

		public void gaze(ScreenRegion region, Current __current) {
			if (paused) return;
			
        	synchronized(values) {
	        	if (region != ScreenRegion.ScreenUnknown) {
	        		values.put("gaze_screen", true);
	        		
	        		synchronized(objectLocations) {
	        			for (String objId : objectLocations.keySet()) {
	        				if (objectLocations.get(objId) == region) {
	        					if (objId.equals(AGENT_ID)) {
	        						values.put("gaze_agent", true);
	        					} else if (objId.equals(targetObjId)) {
	        						values.put("gaze_target", true);
	        					}
        						values.put("gaze_anything", true);
	        				}
	        			}
	        		}
	        	}
        	}
        }

        public void userExpression(FacialExpression expression, Current __current) {
        }

        public void userLocation(double x, double y, double z, Current __current) {
        }

		public void faceSeen(boolean faceLeft, boolean faceRight,
				boolean faceMiddle, Current current) {
			if (paused) return;
			
			synchronized(values) {
				values.put("face_left", faceLeft);
				values.put("face_right", faceRight);
				values.put("face_middle", faceMiddle);
			}
		}
        
    }
    
    private class TouchListenerImpl extends _TouchListenerDisp {

        public void click(int x, int y, int width, int height, Current __current) {
			if (paused) return;
			
        	values.put("touch_anything", true);
        }

        public void pointDown(int id, int x, int y, int width, int height, Current __current) {
        }

        public void pointMoved(int id, int newX, int newY, int newWidth, int newHeight,
                Current __current) {
        }

        public void pointUp(int id, Current __current) {
			if (paused) return;
			
        	values.put("touch_anything", true);
        }
        
    }
    
    private class RenderingListenerImpl extends _RenderingListenerDisp {

        public void objectAdded(String objId, Map<String, String> props, Current __current) {
        }

        public void objectRemoved(String objId, Current __current) {
			if (paused) return;
			
        	objectLocations.remove(objId);
        }

        public void objectPropertyChanged(String objId, String propName, String propValue,
                Current __current) {
			if (paused) return;
			
        	if (propName.equals("ScreenRegion")) {
        		objectLocations.put(objId, ScreenRegion.valueOf(propValue));
        	}
        }

        public void userStarted(String name, Current __current) {
			if (paused) return;
			
        	synchronized(values) {
	        	values.put("touch_anything", true);
	        	values.put("touch_target", true);
        	}
        }

        public void userTouchedObject(String objId, Current __current) {
			if (paused) return;
			
        	values.put("touch_anything", true);
        	if (objId.equals(targetObjId)) {
        		values.put("touch_target", true);
        	}
        }

        public void agentAdded(String agentId, Map<String, String> props, Current __current) {
        }

        public void agentRemoved(String agentId, Current __current) {
			if (paused) return;
			
        	objectLocations.remove(AGENT_ID);
        }

        public void agentPropertyChanged(String agentId, String propName, String propValue,
                Current __current) {
			if (paused) return;
			
        	if (propName.equals("ScreenRegion")) {
        		objectLocations.put(AGENT_ID, ScreenRegion.valueOf(propValue));
        	}
        }

        public void worldPropertyChanged(String propName, String propValue, Current __current) {
        }

        public void scenarioStarted(String name, Current __current) {
        }

        public void scenarioEnded(String name, Current __current) {
        }

		public void userTouchedAgent(String agentId, Current current) {
			if (paused) return;
			
			values.put("touch_anything", true);
		}
        
    }

	public void setTargetObject(String objId) {
		this.targetObjId = objId;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}


}
