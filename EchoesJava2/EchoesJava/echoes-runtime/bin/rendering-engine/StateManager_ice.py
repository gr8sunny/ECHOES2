# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `StateManager.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('StateManager'):
    _M_echoes.StateManager = Ice.createTempClass()
    class StateManager(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.StateManager:
                raise RuntimeError('echoes.StateManager is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::StateManager')

        def ice_id(self, current=None):
            return '::echoes::StateManager'

        def ice_staticId():
            return '::echoes::StateManager'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def getGazeRegion(self, current=None):
        # def getGazeObject(self, current=None):
        # def getObjects(self, current=None):
        # def getAgents(self, current=None):
        # def getProperties(self, objId, current=None):
        # def getUserName(self, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_StateManager)

        __repr__ = __str__

    _M_echoes.StateManagerPrx = Ice.createTempClass()
    class StateManagerPrx(Ice.ObjectPrx):

        def getGazeRegion(self, _ctx=None):
            return _M_echoes.StateManager._op_getGazeRegion.invoke(self, ((), _ctx))

        def getGazeObject(self, _ctx=None):
            return _M_echoes.StateManager._op_getGazeObject.invoke(self, ((), _ctx))

        def getObjects(self, _ctx=None):
            return _M_echoes.StateManager._op_getObjects.invoke(self, ((), _ctx))

        def getAgents(self, _ctx=None):
            return _M_echoes.StateManager._op_getAgents.invoke(self, ((), _ctx))

        def getProperties(self, objId, _ctx=None):
            return _M_echoes.StateManager._op_getProperties.invoke(self, ((objId, ), _ctx))

        def getUserName(self, _ctx=None):
            return _M_echoes.StateManager._op_getUserName.invoke(self, ((), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.StateManagerPrx.ice_checkedCast(proxy, '::echoes::StateManager', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.StateManagerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_StateManagerPrx = IcePy.defineProxy('::echoes::StateManager', StateManagerPrx)

    _M_echoes._t_StateManager = IcePy.defineClass('::echoes::StateManager', StateManager, (), True, None, (), ())
    StateManager.ice_type = _M_echoes._t_StateManager

    StateManager._op_getGazeRegion = IcePy.Operation('getGazeRegion', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (((), _M_echoes._t_ScreenLocation), ((), IcePy._t_double), ((), IcePy._t_long)), None, ())
    StateManager._op_getGazeObject = IcePy.Operation('getGazeObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (((), IcePy._t_string), ((), IcePy._t_double), ((), IcePy._t_long)), None, ())
    StateManager._op_getObjects = IcePy.Operation('getObjects', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), _M_echoes._t_StringSeq, ())
    StateManager._op_getAgents = IcePy.Operation('getAgents', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), _M_echoes._t_StringSeq, ())
    StateManager._op_getProperties = IcePy.Operation('getProperties', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), _M_echoes._t_Properties, ())
    StateManager._op_getUserName = IcePy.Operation('getUserName', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_string, ())

    _M_echoes.StateManager = StateManager
    del StateManager

    _M_echoes.StateManagerPrx = StateManagerPrx
    del StateManagerPrx

# End of module echoes
