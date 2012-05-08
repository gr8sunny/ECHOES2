'''
Created on 14 Oct 2009

@author: cfabric
'''

from EchoesSceneElement import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *
from visual.gltext import *
import Logger
import wx

class UserMenu(EchoesSceneElement):
    '''
    classdocs
    '''

    def __init__(self, app, autoAdd=True, fadeIn=False, fadingFrames=100, userlist=[], pos = (0.0,0.0,0.0), fontsize =0.5):
        '''
        Constructor
        '''
        super(UserMenu, self).__init__(app, autoAdd, fadeIn, fadingFrames)
        self.pos = pos
        self.fontsize = fontsize
        self.newline = self.fontsize * 1.5
        self.userlist = userlist
        self.selection = -1

    def __setattr__(self, item, value):
        if item == "userlist":
            self.utexts = []
            for user in value:
                self.utexts.append(Text(user, font_size = 50))
            self.firstPos = [self.pos[0], self.pos[1] + len(value) * self.newline / 2.0 - self.fontsize*1.5]
        
        object.__setattr__(self, item, value)
                               
    def renderObj(self):
        if not hasattr(self, "utexts"): return
        nl = 0
        for user in self.utexts:
            user.draw_text((self.firstPos[0],self.firstPos[1]-nl), 1.0/50 * self.fontsize)
            nl += self.newline        
            
    def click(self, pos):
        index = 0
        nl = 0
        for user in self.utexts:
            w , h = user._aloc_text._texture_size
            w *= 1.0/50 * self.fontsize
            h *= 1.0/50 * self.fontsize
            x , y = (self.firstPos[0],self.firstPos[1]-nl)
            if pos[0] > x and pos[0] < x+w and pos[1] > y and pos[1] < y+h:
                Logger.trace("info", "Selected " + self.userlist[index])
                self.selection = index
                self.app.canvas.rlPublisher.userStarted(self.userlist[index])
                break
            index +=1
            nl += self.newline
                        
        
