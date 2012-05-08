'''
Created on 12 Oct 2009

@author: cfabric
'''

import math, random

class BezierMotion(object):

    def initBezierVars(self):
        '''
        Constructor replacement (multiple inheritance stuff...)
        NOTE: Must be used in combination with either an EchoesObject or EchoesAgent (needs self.app!)
        '''
        self.speed = 0.01
        self.bezierIndex = 0.0
        self.ctrlpoints = dict()
        self.targetPos = None
        self.boundingBox = self.app.canvas.getRegionCoords("all")     # bounding box in which the object should move
        self.boundingBoxFactor = 0.8    # smaller bounding box in which the control points will be
        self.overshooting = 0.3 # potentially producing the first control point outside the bounding box to smooth transition between two beziers
        self.showCtrlPoints = False

        
    def nextBezierPos(self, flat=False):
        x = math.pow(1-self.bezierIndex, 3) * self.ctrlpoints[0][0] + 3 * math.pow(1-self.bezierIndex, 2) * self.bezierIndex * self.ctrlpoints[1][0] + 3 * (1-self.bezierIndex) * math.pow(self.bezierIndex,2) * self.ctrlpoints[2][0] + math.pow(self.bezierIndex, 3) * self.ctrlpoints[3][0]
        y = math.pow(1-self.bezierIndex, 3) * self.ctrlpoints[0][1] + 3 * math.pow(1-self.bezierIndex, 2) * self.bezierIndex * self.ctrlpoints[1][1] + 3 * (1-self.bezierIndex) * math.pow(self.bezierIndex,2) * self.ctrlpoints[2][1] + math.pow(self.bezierIndex, 3) * self.ctrlpoints[3][1]
        if not flat:
            z = math.pow(1-self.bezierIndex, 3) * self.ctrlpoints[0][2] + 3 * math.pow(1-self.bezierIndex, 2) * self.bezierIndex * self.ctrlpoints[1][2] + 3 * (1-self.bezierIndex) * math.pow(self.bezierIndex,2) * self.ctrlpoints[2][2] + math.pow(self.bezierIndex, 3) * self.ctrlpoints[3][2]
        else:
            z = self.pos[2]
        self.bezierIndex += self.speed
        if self.bezierIndex > 1.0:
            self.newctrlpoints((x,y,z))
        return (x,y,z)
    
    def setStartPos(self, pos):
        self.pos = pos
        self.ctrlpoints.clear()
        self.newctrlpoints()
            
    def setTargetPos(self, pos):
        self.targetPos = pos
        self.newctrlpoints()    

    def newctrlpoints(self, latestpos=None):
        self.bezierIndex = 0.0
        if not latestpos:
            self.ctrlpoints[0] = self.pos
        else: 
            self.ctrlpoints[0] = latestpos
        try:
            self.ctrlpoints[1] = (self.pos[0] + (self.pos[0]-self.ctrlpoints[2][0])*self.overshooting,
                                  self.pos[1] + (self.pos[1]-self.ctrlpoints[2][1])*self.overshooting, 
                                  self.pos[2] + (self.pos[2]-self.ctrlpoints[2][2])*self.overshooting)
        except KeyError:
            self.ctrlpoints[1] = self.randompoint()
        self.ctrlpoints[2] = self.randompoint()
        if self.targetPos:
            self.ctrlpoints[3] = self.targetPos
            self.targetPos = None
        else:
            self.ctrlpoints[3] = self.randompoint()
                                    
    def randompoint(self):    
        x = self.boundingBox[0][0] + (self.boundingBox[1][0] - self.boundingBox[0][0]) * ((1.0-self.boundingBoxFactor)/2 + self.boundingBoxFactor*random.random()) 
        y = self.boundingBox[0][1] + (self.boundingBox[1][1] - self.boundingBox[0][1]) * ((1.0-self.boundingBoxFactor)/2 + self.boundingBoxFactor*random.random())          
        z = self.boundingBox[0][2] + (self.boundingBox[1][2] - self.boundingBox[0][2]) * ((1.0-self.boundingBoxFactor)/2 + self.boundingBoxFactor*random.random())         
        return (x,y,z)
