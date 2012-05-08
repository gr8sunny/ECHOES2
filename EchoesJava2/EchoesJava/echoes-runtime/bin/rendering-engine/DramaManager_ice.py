# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `DramaManager.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('DramaManager'):
    _M_echoes.DramaManager = Ice.createTempClass()
    class DramaManager(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.DramaManager:
                raise RuntimeError('echoes.DramaManager is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::DramaManager')

        def ice_id(self, current=None):
            return '::echoes::DramaManager'

        def ice_staticId():
            return '::echoes::DramaManager'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def getCurrentScene(self, current=None):
        # def setScene(self, scene, current=None):
        # def setIntroScene(self, scene, childName, current=None):
        # def setBubbleSceneParameters(self, numBubbles, displayScore, current=None):
        # def addObject(self, objectType, current=None):
        # def removeObject(self, objId, current=None):
        # def arrangeScene(self, scene, activity, numRepetitions, contingent, current=None):
        # def getTargetObject(self, objectType, contact, current=None):
        # def setActivityStarted(self, activityStarted, current=None):
        # def getObjectType(self, objId, current=None):
        # def dimScene(self, current=None):
        # def updateObjectLocation(self, objId, propValue, current=None):
        # def moveFlower(self, flowerId, current=None):
        # def setFlowerLoc(self, flowerId, current=None):
        # def setFlowerInteractivity(self, flower, movable, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_DramaManager)

        __repr__ = __str__

    _M_echoes.DramaManagerPrx = Ice.createTempClass()
    class DramaManagerPrx(Ice.ObjectPrx):

        def getCurrentScene(self, _ctx=None):
            return _M_echoes.DramaManager._op_getCurrentScene.invoke(self, ((), _ctx))

        def setScene(self, scene, _ctx=None):
            return _M_echoes.DramaManager._op_setScene.invoke(self, ((scene, ), _ctx))

        def setIntroScene(self, scene, childName, _ctx=None):
            return _M_echoes.DramaManager._op_setIntroScene.invoke(self, ((scene, childName), _ctx))

        def setBubbleSceneParameters(self, numBubbles, displayScore, _ctx=None):
            return _M_echoes.DramaManager._op_setBubbleSceneParameters.invoke(self, ((numBubbles, displayScore), _ctx))

        def addObject(self, objectType, _ctx=None):
            return _M_echoes.DramaManager._op_addObject.invoke(self, ((objectType, ), _ctx))

        def removeObject(self, objId, _ctx=None):
            return _M_echoes.DramaManager._op_removeObject.invoke(self, ((objId, ), _ctx))

        def arrangeScene(self, scene, activity, numRepetitions, contingent, _ctx=None):
            return _M_echoes.DramaManager._op_arrangeScene.invoke(self, ((scene, activity, numRepetitions, contingent), _ctx))

        def getTargetObject(self, objectType, contact, _ctx=None):
            return _M_echoes.DramaManager._op_getTargetObject.invoke(self, ((objectType, contact), _ctx))

        def setActivityStarted(self, activityStarted, _ctx=None):
            return _M_echoes.DramaManager._op_setActivityStarted.invoke(self, ((activityStarted, ), _ctx))

        def getObjectType(self, objId, _ctx=None):
            return _M_echoes.DramaManager._op_getObjectType.invoke(self, ((objId, ), _ctx))

        def dimScene(self, _ctx=None):
            return _M_echoes.DramaManager._op_dimScene.invoke(self, ((), _ctx))

        def updateObjectLocation(self, objId, propValue, _ctx=None):
            return _M_echoes.DramaManager._op_updateObjectLocation.invoke(self, ((objId, propValue), _ctx))

        def moveFlower(self, flowerId, _ctx=None):
            return _M_echoes.DramaManager._op_moveFlower.invoke(self, ((flowerId, ), _ctx))

        def setFlowerLoc(self, flowerId, _ctx=None):
            return _M_echoes.DramaManager._op_setFlowerLoc.invoke(self, ((flowerId, ), _ctx))

        def setFlowerInteractivity(self, flower, movable, _ctx=None):
            return _M_echoes.DramaManager._op_setFlowerInteractivity.invoke(self, ((flower, movable), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.DramaManagerPrx.ice_checkedCast(proxy, '::echoes::DramaManager', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.DramaManagerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_DramaManagerPrx = IcePy.defineProxy('::echoes::DramaManager', DramaManagerPrx)

    _M_echoes._t_DramaManager = IcePy.defineClass('::echoes::DramaManager', DramaManager, (), True, None, (), ())
    DramaManager.ice_type = _M_echoes._t_DramaManager

    DramaManager._op_getCurrentScene = IcePy.Operation('getCurrentScene', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), _M_echoes._t_EchoesScene, ())
    DramaManager._op_setScene = IcePy.Operation('setScene', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesScene),), (), None, ())
    DramaManager._op_setIntroScene = IcePy.Operation('setIntroScene', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesScene), ((), IcePy._t_string)), (), None, ())
    DramaManager._op_setBubbleSceneParameters = IcePy.Operation('setBubbleSceneParameters', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int), ((), IcePy._t_bool)), (), None, ())
    DramaManager._op_addObject = IcePy.Operation('addObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesObjectType),), (), None, ())
    DramaManager._op_removeObject = IcePy.Operation('removeObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    DramaManager._op_arrangeScene = IcePy.Operation('arrangeScene', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesScene), ((), _M_echoes._t_EchoesActivity), ((), IcePy._t_int), ((), IcePy._t_bool)), (), None, ())
    DramaManager._op_getTargetObject = IcePy.Operation('getTargetObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesObjectType), ((), IcePy._t_bool)), (), IcePy._t_string, ())
    DramaManager._op_setActivityStarted = IcePy.Operation('setActivityStarted', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_bool),), (), None, ())
    DramaManager._op_getObjectType = IcePy.Operation('getObjectType', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), IcePy._t_string, ())
    DramaManager._op_dimScene = IcePy.Operation('dimScene', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), None, ())
    DramaManager._op_updateObjectLocation = IcePy.Operation('updateObjectLocation', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string)), (), None, ())
    DramaManager._op_moveFlower = IcePy.Operation('moveFlower', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    DramaManager._op_setFlowerLoc = IcePy.Operation('setFlowerLoc', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    DramaManager._op_setFlowerInteractivity = IcePy.Operation('setFlowerInteractivity', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_bool)), (), None, ())

    _M_echoes.DramaManager = DramaManager
    del DramaManager

    _M_echoes.DramaManagerPrx = DramaManagerPrx
    del DramaManagerPrx

# End of module echoes
