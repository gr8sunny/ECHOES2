# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `Fusion.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import UserHeadListener_ice
import TouchListener_ice
import RenderingListener_ice
import AgentListener_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('Fusion'):
    _M_echoes.Fusion = Ice.createTempClass()
    class Fusion(_M_echoes.UserHeadListener, _M_echoes.RenderingListener, _M_echoes.AgentListener):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.Fusion:
                raise RuntimeError('echoes.Fusion is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::AgentListener', '::echoes::Fusion', '::echoes::RenderingListener', '::echoes::UserHeadListener')

        def ice_id(self, current=None):
            return '::echoes::Fusion'

        def ice_staticId():
            return '::echoes::Fusion'
        ice_staticId = staticmethod(ice_staticId)

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_Fusion)

        __repr__ = __str__

    _M_echoes.FusionPrx = Ice.createTempClass()
    class FusionPrx(_M_echoes.UserHeadListenerPrx, _M_echoes.RenderingListenerPrx, _M_echoes.AgentListenerPrx):

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.FusionPrx.ice_checkedCast(proxy, '::echoes::Fusion', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.FusionPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_FusionPrx = IcePy.defineProxy('::echoes::Fusion', FusionPrx)

    _M_echoes._t_Fusion = IcePy.defineClass('::echoes::Fusion', Fusion, (), True, None, (_M_echoes._t_UserHeadListener, _M_echoes._t_RenderingListener, _M_echoes._t_AgentListener), ())
    Fusion.ice_type = _M_echoes._t_Fusion

    _M_echoes.Fusion = Fusion
    del Fusion

    _M_echoes.FusionPrx = FusionPrx
    del FusionPrx

# End of module echoes
