'''
Created on 14 Oct 2009

@author: cfabric
'''

from EchoesSceneElement import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *


class Axis(EchoesSceneElement):
    '''
    classdocs
    '''

    def __init__(self, app, autoAdd=True, fadeIn=False, fadingFrames=100):
        '''
        Constructor
        '''
        super(Axis, self).__init__(app, autoAdd, fadeIn, fadingFrames)
               
    def renderObj(self):
        glDisable(GL_LIGHTING)
        # Draw the x-axis in red
        glColor4f(1.0, 0.0, 0.0, self.transperancy);
        glBegin(GL_LINES);
        glVertex3f(0,0,0);
        glVertex3f(1,0,0)
        glEnd()
        glBegin(GL_TRIANGLE_FAN)
        glVertex3f(1.0, 0.0,  0.0 )
        glVertex3f(0.8, 0.07, 0.0 )
        glVertex3f(0.8, 0.0,  0.07)
        glVertex3f(0.8,-0.07, 0.0 )
        glVertex3f(0.8, 0.0, -0.07)
        glVertex3f(0.8, 0.07, 0.0 )
        glEnd()
    
        # Draw the y-axis in green
        glColor4f(0.0, 1.0, 0.0, self.transperancy)
        glBegin(GL_LINES)
        glVertex3f(0,0,0)
        glVertex3f(0,1,0)
        glEnd()
        glBegin(GL_TRIANGLE_FAN)
        glVertex3f( 0.0,  1.0, 0.0 )
        glVertex3f( 0.07, 0.8, 0.0 )
        glVertex3f( 0.0,  0.8, 0.07)
        glVertex3f(-0.07, 0.8, 0.0 )
        glVertex3f( 0.0,  0.8,-0.07)
        glVertex3f( 0.07, 0.8, 0.0 )
        glEnd()
    
        # Draw the z-axis in blue
        glColor4f(0.0, 0.0, 1.0, self.transperancy)
        glBegin(GL_LINES)
        glVertex3f(0,0,0)
        glVertex3f(0,0,1)
        glEnd()
        glBegin(GL_TRIANGLE_FAN)
        glVertex3f( 0.0,  0.0,  1.0)
        glVertex3f( 0.07, 0.0,  0.8)
        glVertex3f( 0.0,  0.07, 0.8)
        glVertex3f(-0.07, 0.0,  0.8)
        glVertex3f( 0.0, -0.07, 0.8)
        glVertex3f( 0.07, 0.0,  0.8)
        glEnd()
        glEnable(GL_LIGHTING)


class Grid3x3(EchoesSceneElement):
    '''
    classdocs
    '''

    def __init__(self, app, autoAdd=True, fadeIn=False, fadingFrames=100):
        '''
        Constructor
        '''
        super(Grid3x3, self).__init__(app, autoAdd, fadeIn, fadingFrames)
               
    def renderObj(self):
        w = self.app.canvas.orthoCoordWidth
        h = self.app.canvas.orthoCoordWidth / self.app.canvas.aspectRatio
        d = self.app.canvas.orthoCoordDepth

        glDisable(GL_LIGHTING)
        # Draw the x-axis in red
        glColor4f(1.0, 1.0, 0.0, 0.1*self.transperancy);
        for i in [-1.0,1.0]:
            glBegin(GL_LINES);
            glVertex3f(i*w/6,h/2,0);
            glVertex3f(i*w/6,-1*h/2,0)
            glEnd()
            glBegin(GL_LINES);
            glVertex3f(-1*w/2,i*h/6,0);
            glVertex3f(w/2,i*h/6,0)
            glEnd()

        
        glEnable(GL_LIGHTING)


class Score(EchoesSceneElement):
    '''
    classdocs
    '''

    def __init__(self, app, autoAdd=True, fadeIn=False, fadingFrames=100, fontsize = 1):
        '''
        Constructor
        '''
        super(Score, self).__init__(app, autoAdd, fadeIn, fadingFrames)
        self.pos = (-1*self.app.canvas.orthoCoordWidth/2.0 + 0.2, -1 * self.app.canvas.orthoCoordWidth / self.app.canvas.aspectRatio / 4.0 - fontsize, 10)
        self.fontsize = fontsize
        self.roman_charheight = 119.05 + 33.33
        self.roman_charwidth = 104.76
        self.score = 0
        
    def __setattr__(self, item, value):
        if item == "score":        
            self.app.canvas.rlPublisher.worldPropertyChanged("Score", str(value))

        object.__setattr__(self, item, value)
                       
    def renderObj(self):
        
        charscale = self.fontsize / self.roman_charheight   
        glLineWidth(8.0)
        glColor4f(0.141, 0.278, 0.929, 0.4*self.transperancy)
        glPushMatrix()
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_LIGHTING)        
        glTranslatef(self.pos[0],self.pos[1],self.pos[2])
        glScalef(charscale, charscale, charscale)
        for letter in list(str(self.score)):
            glutStrokeCharacter(GLUT_STROKE_ROMAN, ord(letter))
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_LIGHTING)
        glPopMatrix()        
        glLineWidth(1.0)


    def setScore(self, score):
        self.score = score
        
    def reset(self):
        self.score = 0
        
    def increment(self):
        self.score += 1