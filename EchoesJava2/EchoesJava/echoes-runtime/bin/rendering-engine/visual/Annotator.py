'''
Created on 19 Oct 2010

@author: cfabric
'''

from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *
import PIL.Image
import echoes
import numpy, math

class Annotator():
    
    def __init__(self, app):
        '''
        Constructor
        '''
        self.app = app
        self.w2 = 0.5*app.canvas.orthoCoordWidth
        self.h2 = 0.5*app.canvas.orthoCoordWidth/app.canvas.aspectRatio
        self.fw = 0.1
        self.frame = [[(-self.w2+self.fw, self.h2), (self.w2-self.fw, self.h2), (self.w2-self.fw, self.h2-self.fw), (-self.w2+self.fw, self.h2-self.fw)],
                      [(self.w2, self.h2), (self.w2, -self.h2), (self.w2-self.fw, -self.h2), (self.w2-self.fw, self.h2)],
                      [(self.w2-self.fw, -self.h2), (-self.w2+self.fw, -self.h2), (-self.w2+self.fw, -self.h2+self.fw), (self.w2-self.fw, -self.h2+self.fw)],
                      [(-self.w2, -self.h2), (-self.w2, self.h2), (-self.w2+self.fw, self.h2), (-self.w2+self.fw, -self.h2)]] 
        
        
        self.shape = [(-1, -1), (1, -1), (1, 1), (-1, 1)]
        self.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]


        self.emoticons = dict()
        self.strokes = [[]]
        
        self.drawingFeature(True)
    
    def loadTextures(self, images=["emoticon_happy.png", "emoticon_sad.png", "pencil-icon.png"]):
        self.textures = glGenTextures(len(images))
        self.boundaries = []
        i = 0
        for image in images:
            try:
                im = PIL.Image.open("visual/images/" + image)
            except IOError:
                Logger.warning("Could not find icon " + image + " for Annotator")
            try:                
                ix, iy, idata = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
            except SystemError:
                ix, iy, idata = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

            glPixelStorei(GL_UNPACK_ALIGNMENT,1)
            glBindTexture(GL_TEXTURE_2D, self.textures[i])
            glTexImage2D(GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL_RGBA, GL_UNSIGNED_BYTE, idata)
            i += 1
        
        
    def render(self):
        glPushMatrix()
        glDisable(GL_DEPTH_TEST)
            # Toolbar
        glColor4f(0.927, 0, 1, 0.5)            
        glBegin( GL_QUADS )
        for v in self.toolsframe:
            glVertex(v[0],v[1], 0.0)
        glEnd()
            # Frame
        for rec in self.frame:
            glBegin( GL_QUADS )
            for v in rec:
                glVertex(v[0],v[1], 0.0)
            glEnd()
            # Buttons
        glColor4f(1, 1, 1, 1)
        glPushMatrix()
        glTranslate(self.toolsframe[3][0], self.toolsframe[3][1], 0)
        for i in range(len(self.textures)):
            if i>1 and not self.drawingEnabled:
                break
            self.renderEmoticon((0.25+0.6*i, 0.25, 0), 0.25, self.textures[i])
        glPopMatrix()
            # Emoticons drawn 
        for pos, type in self.emoticons.items():
            if type == 'happy': tex = self.textures[0]
            elif type == 'sad': tex = self.textures[1]
            else: tex = self.textures[3]
            self.renderEmoticon((pos[0], pos[1], 0), 0.15, tex, 0.5)
           # Drawing
        glColor4f(0.927, 0, 1, 1)            
        glLineWidth(3.0)
        for stroke in self.strokes:
            glBegin( GL_LINE_STRIP )
            for v in stroke:
                glVertex(v[0],v[1], 0.0)
            glEnd()
            
        glEnable(GL_DEPTH_TEST)                    
        glPopMatrix()
    
    def renderEmoticon(self, pos, size, tex, transparency=1.0):
        glPushMatrix()
        glEnable( GL_ALPHA_TEST )
        glAlphaFunc( GL_GREATER, 0.1 )        
        glEnable( GL_TEXTURE_2D )
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glBindTexture(GL_TEXTURE_2D, tex)
        glTranslate(pos[0], pos[1], pos[2])
        glScalef(size, size, size)
        glColor4f(1, 1, 1, transparency)
        glBegin(GL_QUADS)
        ti = 0
        for v in self.shape:
            glTexCoord2d(self.texshape[ti][0], self.texshape[ti][1])
            glVertex3f(v[0], v[1], pos[2])
            ti += 1
        glEnd()
        glDisable( GL_TEXTURE_2D )
        glDisable( GL_ALPHA_TEST )
        glPopMatrix()        
    
    def drawingFeature(self, v=True):
        self.drawingEnabled = v
        if v:
            self.loadTextures(["emoticon_happy.png", "emoticon_sad.png", "pencil-icon.png"])
            self.activeTool = "draw"
            self.toolsframe = [(-self.w2+self.fw, self.h2-self.fw), (-self.w2+self.fw+1.7, self.h2-self.fw),  (-self.w2+self.fw+1.7, self.h2-self.fw-0.5),  (-self.w2+self.fw, self.h2-self.fw-0.5)]
        else: 
            self.loadTextures(["emoticon_happy.png", "emoticon_sad.png"])
            self.activeTool = "happy"
            self.toolsframe = [(-self.w2+self.fw, self.h2-self.fw), (-self.w2+self.fw+1.2, self.h2-self.fw),  (-self.w2+self.fw+1.2, self.h2-self.fw-0.5),  (-self.w2+self.fw, self.h2-self.fw-0.5)]
    
    def startDrag(self, pos):
        pos = self.app.canvas.getWorldCoord(pos)
        buttonPressed = False
        for i in range(len(self.textures)):
            x = self.toolsframe[3][0] + i*0.6
            if pos[0] > x and pos[0] < (x+0.5) and pos[1] > self.toolsframe[3][1] and pos[1] < self.toolsframe[1][1]:
                buttonPressed = True
                if i== 2:
                    self.activeTool = "draw"
                elif i== 1:
                    self.activeTool = "sad"
                else:
                    self.activeTool = "happy"
                    
        if not buttonPressed:
            if self.activeTool != "draw":
                self.emoticons[pos] = self.activeTool
    
    def stopDrag(self):
        if self.activeTool == "draw":
            self.strokes.append([])
    
    def drag(self, pos):
        # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL_VIEWPORT)
        windowZ = glReadPixels(pos[0], viewport[3]-pos[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
        
        worldCoords = gluUnProject(pos[0], viewport[3] - pos[1], windowZ[0][0], modelview, projection, viewport)

        if self.activeTool == "draw":
            self.strokes[len(self.strokes)-1].append(worldCoords)
    
    def click(self, pos):
        pass
    
    def done(self):
        self.app.canvas.saveScreenshot()