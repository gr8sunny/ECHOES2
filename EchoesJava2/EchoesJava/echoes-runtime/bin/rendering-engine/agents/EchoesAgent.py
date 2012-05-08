'''
Created on Sep 29, 2009

@author: cfabric
'''

class EchoesAgent(object):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "None"}):
        '''
        Constructor
        '''
        self.app = app
        self.props = props
        if autoAdd:
            self.id = app.canvas.addAgent(self, props)
        else:
            self.id = -1
        
        self.collisonTest = True            # whether to include in any collision testing

            
    def render(self):
        '''
        All objects are added to the scene as plugins (see EchoesGLCanvas) 
        and need to provide a render function if they want to draw anything 
        '''
        print "EchoesAgent: overwrite this rendering method!"
        
    def remove(self):
        # Remove this object from the canvas
        self.app.canvas.removeAgent(self.id)


class AgentAction(object):
    
    def __init__(self, callback, agentId, action, details):
        self.callback = callback
        self.agentId = agentId
        self.action = action
        self.details = details
