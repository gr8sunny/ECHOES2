'''
Created on 14 Oct 2009

@author: cfabric
'''

class EchoesSceneElement(object):
    '''
    classdocs
    '''


    def __init__(self, app, autoAdd=True, fadeIn = False, fadingFrames = 100):
        '''
        Constructor
        '''
        self.app = app
        if autoAdd:
            self.id = app.canvas.addSceneElement(self)
        else:
            self.id = -1

        self.fadingOut = False
        self.fadingIn = fadeIn
        self.fadingFrames = fadingFrames
        if fadeIn:
            self.transperancy = 0.0
        else:
            self.transperancy = 1.0

        
    def render(self):
        if hasattr(self, "fadingIn") and self.fadingIn:
            self.transperancy += 1.0/self.fadingFrames
            if self.transperancy >= 1.0:
                self.transperancy = 1.0
                self.fadingIn = False

        elif hasattr(self, "fadingOut") and self.fadingOut:
            self.transperancy -= 1.0/self.fadingFrames
            if self.transperancy <= 0.0:
                self.remove(False)
        
        self.renderObj()

    def renderObj(self):
        '''
        Overwrite this to render the element from the plugin system in EchoesGLCanvas
        '''
        print "Overwrite this method to render scene element"


    def remove(self, fadeOut=False, fadingFrames=100):
        if fadeOut:
            self.fadingOut = True
            self.fadingFrames = fadingFrames
            self.transperancy = 1.0
        else:
            self.app.canvas.removeSceneElement(self.id)
