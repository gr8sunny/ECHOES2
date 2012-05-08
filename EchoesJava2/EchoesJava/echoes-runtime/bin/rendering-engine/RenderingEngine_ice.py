# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `RenderingEngine.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('_t_ObjectList'):
    _M_echoes._t_ObjectList = IcePy.defineSequence('::echoes::ObjectList', (), IcePy._t_Object)

if not _M_echoes.__dict__.has_key('RenderingEngine'):
    _M_echoes.RenderingEngine = Ice.createTempClass()
    class RenderingEngine(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.RenderingEngine:
                raise RuntimeError('echoes.RenderingEngine is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::RenderingEngine')

        def ice_id(self, current=None):
            return '::echoes::RenderingEngine'

        def ice_staticId():
            return '::echoes::RenderingEngine'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def loadScenario_async(self, _cb, name, current=None):
        # def endScenario_async(self, _cb, name, current=None):
        # def addObject_async(self, _cb, objectType, current=None):
        # def removeObject_async(self, _cb, objId, current=None):
        # def setWorldProperty(self, propName, propValue, current=None):
        # def setObjectProperty(self, objId, propName, propValue, current=None):
        # def addAgent_async(self, _cb, agentType, current=None):
        # def addAgentWithPose_async(self, _cb, agentType, pose, current=None):
        # def removeAgent(self, agentId, current=None):
        # def executeAction_async(self, _cb, agentId, action, details, current=None):
        # def getAttentionProbability(self, objectId, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_RenderingEngine)

        __repr__ = __str__

    _M_echoes.RenderingEnginePrx = Ice.createTempClass()
    class RenderingEnginePrx(Ice.ObjectPrx):

        def loadScenario(self, name, _ctx=None):
            return _M_echoes.RenderingEngine._op_loadScenario.invoke(self, ((name, ), _ctx))

        def endScenario(self, name, _ctx=None):
            return _M_echoes.RenderingEngine._op_endScenario.invoke(self, ((name, ), _ctx))

        def addObject(self, objectType, _ctx=None):
            return _M_echoes.RenderingEngine._op_addObject.invoke(self, ((objectType, ), _ctx))

        def removeObject(self, objId, _ctx=None):
            return _M_echoes.RenderingEngine._op_removeObject.invoke(self, ((objId, ), _ctx))

        def setWorldProperty(self, propName, propValue, _ctx=None):
            return _M_echoes.RenderingEngine._op_setWorldProperty.invoke(self, ((propName, propValue), _ctx))

        def setObjectProperty(self, objId, propName, propValue, _ctx=None):
            return _M_echoes.RenderingEngine._op_setObjectProperty.invoke(self, ((objId, propName, propValue), _ctx))

        def addAgent(self, agentType, _ctx=None):
            return _M_echoes.RenderingEngine._op_addAgent.invoke(self, ((agentType, ), _ctx))

        def addAgentWithPose(self, agentType, pose, _ctx=None):
            return _M_echoes.RenderingEngine._op_addAgentWithPose.invoke(self, ((agentType, pose), _ctx))

        def removeAgent(self, agentId, _ctx=None):
            return _M_echoes.RenderingEngine._op_removeAgent.invoke(self, ((agentId, ), _ctx))

        def executeAction(self, agentId, action, details, _ctx=None):
            return _M_echoes.RenderingEngine._op_executeAction.invoke(self, ((agentId, action, details), _ctx))

        def getAttentionProbability(self, objectId, _ctx=None):
            return _M_echoes.RenderingEngine._op_getAttentionProbability.invoke(self, ((objectId, ), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.RenderingEnginePrx.ice_checkedCast(proxy, '::echoes::RenderingEngine', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.RenderingEnginePrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_RenderingEnginePrx = IcePy.defineProxy('::echoes::RenderingEngine', RenderingEnginePrx)

    _M_echoes._t_RenderingEngine = IcePy.defineClass('::echoes::RenderingEngine', RenderingEngine, (), True, None, (), ())
    RenderingEngine.ice_type = _M_echoes._t_RenderingEngine

    RenderingEngine._op_loadScenario = IcePy.Operation('loadScenario', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (((), IcePy._t_string),), (), None, ())
    RenderingEngine._op_endScenario = IcePy.Operation('endScenario', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (((), IcePy._t_string),), (), None, ())
    RenderingEngine._op_addObject = IcePy.Operation('addObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (((), IcePy._t_string),), (), IcePy._t_string, ())
    RenderingEngine._op_removeObject = IcePy.Operation('removeObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (((), IcePy._t_string),), (), None, ())
    RenderingEngine._op_setWorldProperty = IcePy.Operation('setWorldProperty', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string)), (), None, ())
    RenderingEngine._op_setObjectProperty = IcePy.Operation('setObjectProperty', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string), ((), IcePy._t_string)), (), None, ())
    RenderingEngine._op_addAgent = IcePy.Operation('addAgent', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (((), IcePy._t_string),), (), IcePy._t_string, ())
    RenderingEngine._op_addAgentWithPose = IcePy.Operation('addAgentWithPose', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (((), IcePy._t_string), ((), IcePy._t_string)), (), IcePy._t_string, ())
    RenderingEngine._op_removeAgent = IcePy.Operation('removeAgent', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), IcePy._t_string, ())
    RenderingEngine._op_executeAction = IcePy.Operation('executeAction', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (((), IcePy._t_string), ((), IcePy._t_string), ((), _M_echoes._t_StringSeq)), (), IcePy._t_bool, ())
    RenderingEngine._op_getAttentionProbability = IcePy.Operation('getAttentionProbability', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), IcePy._t_string, ())

    _M_echoes.RenderingEngine = RenderingEngine
    del RenderingEngine

    _M_echoes.RenderingEnginePrx = RenderingEnginePrx
    del RenderingEnginePrx

# End of module echoes
