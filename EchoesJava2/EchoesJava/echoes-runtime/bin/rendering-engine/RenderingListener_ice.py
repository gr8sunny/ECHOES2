# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `RenderingListener.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('RenderingListener'):
    _M_echoes.RenderingListener = Ice.createTempClass()
    class RenderingListener(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.RenderingListener:
                raise RuntimeError('echoes.RenderingListener is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::RenderingListener')

        def ice_id(self, current=None):
            return '::echoes::RenderingListener'

        def ice_staticId():
            return '::echoes::RenderingListener'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def objectAdded(self, objId, props, current=None):
        # def objectRemoved(self, objId, current=None):
        # def objectPropertyChanged(self, objId, propName, propValue, current=None):
        # def userStarted(self, name, current=None):
        # def userTouchedObject(self, objId, current=None):
        # def userTouchedAgent(self, agentId, current=None):
        # def agentAdded(self, agentId, props, current=None):
        # def agentRemoved(self, agentId, current=None):
        # def agentPropertyChanged(self, agentId, propName, propValue, current=None):
        # def worldPropertyChanged(self, propName, propValue, current=None):
        # def scenarioStarted(self, name, current=None):
        # def scenarioEnded(self, name, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_RenderingListener)

        __repr__ = __str__

    _M_echoes.RenderingListenerPrx = Ice.createTempClass()
    class RenderingListenerPrx(Ice.ObjectPrx):

        def objectAdded(self, objId, props, _ctx=None):
            return _M_echoes.RenderingListener._op_objectAdded.invoke(self, ((objId, props), _ctx))

        def objectRemoved(self, objId, _ctx=None):
            return _M_echoes.RenderingListener._op_objectRemoved.invoke(self, ((objId, ), _ctx))

        def objectPropertyChanged(self, objId, propName, propValue, _ctx=None):
            return _M_echoes.RenderingListener._op_objectPropertyChanged.invoke(self, ((objId, propName, propValue), _ctx))

        def userStarted(self, name, _ctx=None):
            return _M_echoes.RenderingListener._op_userStarted.invoke(self, ((name, ), _ctx))

        def userTouchedObject(self, objId, _ctx=None):
            return _M_echoes.RenderingListener._op_userTouchedObject.invoke(self, ((objId, ), _ctx))

        def userTouchedAgent(self, agentId, _ctx=None):
            return _M_echoes.RenderingListener._op_userTouchedAgent.invoke(self, ((agentId, ), _ctx))

        def agentAdded(self, agentId, props, _ctx=None):
            return _M_echoes.RenderingListener._op_agentAdded.invoke(self, ((agentId, props), _ctx))

        def agentRemoved(self, agentId, _ctx=None):
            return _M_echoes.RenderingListener._op_agentRemoved.invoke(self, ((agentId, ), _ctx))

        def agentPropertyChanged(self, agentId, propName, propValue, _ctx=None):
            return _M_echoes.RenderingListener._op_agentPropertyChanged.invoke(self, ((agentId, propName, propValue), _ctx))

        def worldPropertyChanged(self, propName, propValue, _ctx=None):
            return _M_echoes.RenderingListener._op_worldPropertyChanged.invoke(self, ((propName, propValue), _ctx))

        def scenarioStarted(self, name, _ctx=None):
            return _M_echoes.RenderingListener._op_scenarioStarted.invoke(self, ((name, ), _ctx))

        def scenarioEnded(self, name, _ctx=None):
            return _M_echoes.RenderingListener._op_scenarioEnded.invoke(self, ((name, ), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.RenderingListenerPrx.ice_checkedCast(proxy, '::echoes::RenderingListener', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.RenderingListenerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_RenderingListenerPrx = IcePy.defineProxy('::echoes::RenderingListener', RenderingListenerPrx)

    _M_echoes._t_RenderingListener = IcePy.defineClass('::echoes::RenderingListener', RenderingListener, (), True, None, (), ())
    RenderingListener.ice_type = _M_echoes._t_RenderingListener

    RenderingListener._op_objectAdded = IcePy.Operation('objectAdded', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), _M_echoes._t_Properties)), (), None, ())
    RenderingListener._op_objectRemoved = IcePy.Operation('objectRemoved', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    RenderingListener._op_objectPropertyChanged = IcePy.Operation('objectPropertyChanged', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string), ((), IcePy._t_string)), (), None, ())
    RenderingListener._op_userStarted = IcePy.Operation('userStarted', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    RenderingListener._op_userTouchedObject = IcePy.Operation('userTouchedObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    RenderingListener._op_userTouchedAgent = IcePy.Operation('userTouchedAgent', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    RenderingListener._op_agentAdded = IcePy.Operation('agentAdded', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), _M_echoes._t_Properties)), (), None, ())
    RenderingListener._op_agentRemoved = IcePy.Operation('agentRemoved', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    RenderingListener._op_agentPropertyChanged = IcePy.Operation('agentPropertyChanged', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string), ((), IcePy._t_string)), (), None, ())
    RenderingListener._op_worldPropertyChanged = IcePy.Operation('worldPropertyChanged', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string)), (), None, ())
    RenderingListener._op_scenarioStarted = IcePy.Operation('scenarioStarted', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    RenderingListener._op_scenarioEnded = IcePy.Operation('scenarioEnded', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())

    _M_echoes.RenderingListener = RenderingListener
    del RenderingListener

    _M_echoes.RenderingListenerPrx = RenderingListenerPrx
    del RenderingListenerPrx

# End of module echoes
