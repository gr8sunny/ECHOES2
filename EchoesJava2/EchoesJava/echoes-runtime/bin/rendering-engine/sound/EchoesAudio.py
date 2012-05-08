'''
Created on 2 Mar 2011
 
@author: cfabric
'''

try: 
    import pyo
    pyoPresent = True
except ImportError:
    pyoPresent = False
    
import os, time, sys, threading, wave, audioop
import Ice
import Logger

soundPresent = False
withIce = True
glCanvas = None
mixer = None
server = None
mixerChans = 0
sndpath = 'sound/sounds/'

def soundInit(device=''):
    global soundPresent, mixer, server, pyoPresent
    try:
        pa_device_index = Ice.Application.communicator().getProperties().getPropertyWithDefault('RenderingEngine.PADevice', None)
    except:
        pa_device_index = None

    if pyoPresent:
        try:
            pyo.pa_list_devices()
            server = pyo.Server()
            server.setDuplex(0)
            pai = None
            if pa_device_index:
                try:
                    pai = int(pa_device_index)
                    server.setOutputDevice(pai)
                    Logger.trace("info", "Sound system using device" + str(pai) + " (was defined in RenderingEngine.PADevice)")
                except:
                    Logger.warning("Sound system was passed an invalid device from ICE, trying default device")
            if not pai and hasattr(pyo, "PYO_VERSION") and pyo.PYO_VERSION == "0.4.0": # only try to use ASIO if the new version of pyo is installed
                out_devices = pyo.pa_get_output_devices()
                od_index = 0
                for od in out_devices[0]:
                    if "ASIO" in od:
                        pai = int(out_devices[1][od_index])
                        server.setOutputDevice(pai)
                        Logger.trace("info", "Found ASIO device, using device " + str(pai))                        
                        break
                    od_index += 1
            if not pai:
                Logger.trace("info", "Sound system using default device " + str(pyo.pa_get_default_output()))
            server.boot()
            server.start()
            if server.getIsStarted() == 0:
                Logger.warning("Sound has not started up, trying mono")
                server.setNchnls(1)
                server.boot()
                server.start()
            if server.getIsStarted() == 0:
                Logger.warning("Sound has still not started up, giving up")
                soundPresent = False
            else:
    #            server.gui(locals())
                mixer = pyo.Mixer()
                mixer.out()
                soundPresent = True
                Logger.trace("info", "Sound system up and running")
        except:
            Logger.warning("SoundInit failed, pyaudio could not initialise, sound will not be available")
            soundPresent = False
            raise
    if not soundPresent:
        Logger.warning("pyo is not present, sound will not be available")
                
def soundShutdown():
    global server
    if server: 
        server.shutdown()
        Logger.trace("info", "Sound system is shut down")
    
def getSoundDuration(name):
    global sndpath
    try:
        wr = wave.open(sndpath + "/" + name)
    except:
        Logger.warning("Soundfile not found, no duration returned")
        return 0
    return float(wr.getnframes())/wr.getframerate()

def playSound(name, loop=False, vol=1.0, action_id=-1):
    global mixer, mixerChans
    player = pyo.SfPlayer(sndpath + name, speed=1, loop=loop, mul=vol)
    if not loop:
        time = getSoundDuration(name) + 1.0
        pyo.Clean_objects(time, player).start()
        Clean_mixer(time, mixerChans).start()
    mixer.addInput(mixerChans, player)
    mixer.setAmp(mixerChans,0,1)
    mixer.setAmp(mixerChans,1,1)
    mixerChans +=1
    return player

def bubblePop(size=1.0):
    global mixer, mixerChans
    speed = 1/((size-1.0)*0.5 + 1.0)
    vol = min(size, 1.0)
    player = pyo.SfPlayer(sndpath + "pop.wav", speed=speed, loop=False, mul=vol)
    wet = pyo.Freeverb(player, size=min(size, 1.0), damp=.7, bal=.3)
    pyo.Clean_objects(1.5, wet, player).start()
    Clean_mixer(1.5, mixerChans).start()
    mixer.addInput(mixerChans, wet)
    mixer.setAmp(mixerChans,0,1)
    mixer.setAmp(mixerChans,1,1)
    mixerChans +=1
    return wet
    
def bounce(velocity=1.0):
    global mixer, mixerChans
    speed = 1.2 - velocity
    vol = min(1.0, 2*velocity)
    size = 0.3-velocity
    player = pyo.SfPlayer(sndpath + "bounce.wav", speed=speed, loop=False, mul=vol)
    pyo.Clean_objects(1.5, player).start()
    Clean_mixer(1.5, mixerChans).start()
    mixer.addInput(mixerChans, player)
    mixer.setAmp(mixerChans,0,1)
    mixer.setAmp(mixerChans,1,1)
    mixerChans +=1
    return player

class Clean_mixer(threading.Thread):
    
    global mixer

    def __init__(self, time, mixerChnl):
        self.time = time
        self.mixerChnl = mixerChnl
        threading.Thread.__init__(self)

    def run(self):
        time.sleep(self.time)
        mixer.delInput(self.mixerChnl)

class SoundCallback(threading.Thread):
    
    def __init__(self, sound, callback):
        self.time = getSoundDuration(sound) + 0.1
        self.callback = callback
        threading.Thread.__init__(self)

    def run(self):
        time.sleep(self.time)
        self.callback()
