'''
Created on 14 Oct 2009

@author: cfabric
'''

from EchoesSceneElement import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *
import PIL.Image
import sound.EchoesAudio
import echoes

class Sky(EchoesSceneElement):
    '''
    classdocs
    '''

    def __init__(self, app, autoAdd=True, fadeIn=False, fadingFrames=100):
        '''
        Constructor
        '''
        super(Sky, self).__init__(app, autoAdd, fadeIn, fadingFrames)
              
    def renderObj(self):
 
        glPushMatrix()
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_LIGHTING)
        glScalef(self.app.canvas.orthoCoordWidth/2, self.app.canvas.orthoCoordWidth/2/self.app.canvas.aspectRatio, 1)
        glBegin(GL_QUADS)
        glColor4f(0.303, 0.648, 0.853, self.transperancy)
        glVertex2f(-1,-1)
        glColor4f(0.303, 0.648, 0.853, self.transperancy)
        glVertex2f(1.0,-1.0)
        glColor4f(1, 1, 1, self.transperancy)
        glVertex2f(1.0, 1.0)
        glColor4f(0.303, 0.648, 0.853, self.transperancy)
        glVertex2f(-1.0, 1.0)
        glEnd()
        glEnable(GL_LIGHTING)
        glEnable(GL_DEPTH_TEST)
        glPopMatrix()


class Garden(EchoesSceneElement):
    '''
    classdocs
    '''

    def __init__(self, app, autoAdd=True, fadeIn=False, fadingFrames=100, withSound=True):
        '''
        Constructor
        '''
        super(Garden, self).__init__(app, autoAdd, fadeIn, fadingFrames)
        self.setImage()
        self.withSound = withSound and sound.EchoesAudio.soundPresent
        if self.withSound:
            self.ambient = sound.EchoesAudio.playSound("garden.wav", loop=True, vol = 0.1)        

    def setImage(self, file='visual/images/GardenBackExplore.png'):
        im = PIL.Image.open(file) # .jpg, .bmp, etc. also work
        try:
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
        except SystemError:
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

        self.texture = glGenTextures(1)
        glPixelStorei(GL_UNPACK_ALIGNMENT,1)
        glBindTexture(GL_TEXTURE_2D, self.texture)
        glTexImage2D(GL_TEXTURE_2D, 0, 3, ix, iy, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        
    def renderObj(self):
        glPushMatrix()
        glEnable( GL_TEXTURE_2D )
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glBindTexture(GL_TEXTURE_2D, self.texture)
        glDisable(GL_DEPTH_TEST)
        glColor4f(1,1,1,self.transperancy)
        glScalef(self.app.canvas.orthoCoordWidth/2, self.app.canvas.orthoCoordWidth/2/self.app.canvas.aspectRatio, 1)
        glBegin( GL_QUADS )
        glTexCoord2d(0.0,0.0)
        glVertex2d(-1,-1)
        glTexCoord2d(1.0,0.0)
        glVertex2d(1,-1)
        glTexCoord2d(1.0,1.0)
        glVertex2d(1,1)
        glTexCoord2d(0.0,1.0)
        glVertex2d(-1,1)
        glEnd()        
        glEnable(GL_DEPTH_TEST)
        glDisable( GL_TEXTURE_2D )
        glPopMatrix()
        
    def soundEvent(self, type):
        if self.withSound:
            if type=="plane":
                sound.EchoesAudio.playSound("plane.wav", vol=0.3)
    
    def remove(self, fadeOut = False, fadingFrames = 100):
        if not fadeOut and hasattr(self, "ambient") and self.ambient:
            self.ambient.stop()
        super(Garden, self).remove(fadeOut, fadingFrames)         