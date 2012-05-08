# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `ChildModelListener.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import ChildModel_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('ChildModelListener'):
    _M_echoes.ChildModelListener = Ice.createTempClass()
    class ChildModelListener(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.ChildModelListener:
                raise RuntimeError('echoes.ChildModelListener is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::ChildModelListener')

        def ice_id(self, current=None):
            return '::echoes::ChildModelListener'

        def ice_staticId():
            return '::echoes::ChildModelListener'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def engagementEstimate(self, eng, confidence, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_ChildModelListener)

        __repr__ = __str__

    _M_echoes.ChildModelListenerPrx = Ice.createTempClass()
    class ChildModelListenerPrx(Ice.ObjectPrx):

        def engagementEstimate(self, eng, confidence, _ctx=None):
            return _M_echoes.ChildModelListener._op_engagementEstimate.invoke(self, ((eng, confidence), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.ChildModelListenerPrx.ice_checkedCast(proxy, '::echoes::ChildModelListener', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.ChildModelListenerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_ChildModelListenerPrx = IcePy.defineProxy('::echoes::ChildModelListener', ChildModelListenerPrx)

    _M_echoes._t_ChildModelListener = IcePy.defineClass('::echoes::ChildModelListener', ChildModelListener, (), True, None, (), ())
    ChildModelListener.ice_type = _M_echoes._t_ChildModelListener

    ChildModelListener._op_engagementEstimate = IcePy.Operation('engagementEstimate', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_Engagement), ((), IcePy._t_double)), (), None, ())

    _M_echoes.ChildModelListener = ChildModelListener
    del ChildModelListener

    _M_echoes.ChildModelListenerPrx = ChildModelListenerPrx
    del ChildModelListenerPrx

# End of module echoes
