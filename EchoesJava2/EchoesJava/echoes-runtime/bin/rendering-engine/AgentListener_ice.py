# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `AgentListener.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('AgentListener'):
    _M_echoes.AgentListener = Ice.createTempClass()
    class AgentListener(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.AgentListener:
                raise RuntimeError('echoes.AgentListener is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::AgentListener')

        def ice_id(self, current=None):
            return '::echoes::AgentListener'

        def ice_staticId():
            return '::echoes::AgentListener'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def agentActionStarted(self, agentId, action, details, current=None):
        # def agentActionCompleted(self, agentId, action, details, current=None):
        # def agentActionFailed(self, agentId, action, details, reason, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_AgentListener)

        __repr__ = __str__

    _M_echoes.AgentListenerPrx = Ice.createTempClass()
    class AgentListenerPrx(Ice.ObjectPrx):

        def agentActionStarted(self, agentId, action, details, _ctx=None):
            return _M_echoes.AgentListener._op_agentActionStarted.invoke(self, ((agentId, action, details), _ctx))

        def agentActionCompleted(self, agentId, action, details, _ctx=None):
            return _M_echoes.AgentListener._op_agentActionCompleted.invoke(self, ((agentId, action, details), _ctx))

        def agentActionFailed(self, agentId, action, details, reason, _ctx=None):
            return _M_echoes.AgentListener._op_agentActionFailed.invoke(self, ((agentId, action, details, reason), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.AgentListenerPrx.ice_checkedCast(proxy, '::echoes::AgentListener', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.AgentListenerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_AgentListenerPrx = IcePy.defineProxy('::echoes::AgentListener', AgentListenerPrx)

    _M_echoes._t_AgentListener = IcePy.defineClass('::echoes::AgentListener', AgentListener, (), True, None, (), ())
    AgentListener.ice_type = _M_echoes._t_AgentListener

    AgentListener._op_agentActionStarted = IcePy.Operation('agentActionStarted', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string), ((), _M_echoes._t_StringSeq)), (), None, ())
    AgentListener._op_agentActionCompleted = IcePy.Operation('agentActionCompleted', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string), ((), _M_echoes._t_StringSeq)), (), None, ())
    AgentListener._op_agentActionFailed = IcePy.Operation('agentActionFailed', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string), ((), _M_echoes._t_StringSeq), ((), IcePy._t_string)), (), None, ())

    _M_echoes.AgentListener = AgentListener
    del AgentListener

    _M_echoes.AgentListenerPrx = AgentListenerPrx
    del AgentListenerPrx

# End of module echoes
