# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `PauseListener.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()

# Start of module echoes
_M_echoes = Ice.openModule('echoes')
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('PauseListener'):
    _M_echoes.PauseListener = Ice.createTempClass()
    class PauseListener(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.PauseListener:
                raise RuntimeError('echoes.PauseListener is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::PauseListener')

        def ice_id(self, current=None):
            return '::echoes::PauseListener'

        def ice_staticId():
            return '::echoes::PauseListener'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def setPaused(self, paused, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_PauseListener)

        __repr__ = __str__

    _M_echoes.PauseListenerPrx = Ice.createTempClass()
    class PauseListenerPrx(Ice.ObjectPrx):

        def setPaused(self, paused, _ctx=None):
            return _M_echoes.PauseListener._op_setPaused.invoke(self, ((paused, ), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.PauseListenerPrx.ice_checkedCast(proxy, '::echoes::PauseListener', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.PauseListenerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_PauseListenerPrx = IcePy.defineProxy('::echoes::PauseListener', PauseListenerPrx)

    _M_echoes._t_PauseListener = IcePy.defineClass('::echoes::PauseListener', PauseListener, (), True, None, (), ())
    PauseListener.ice_type = _M_echoes._t_PauseListener

    PauseListener._op_setPaused = IcePy.Operation('setPaused', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_bool),), (), None, ())

    _M_echoes.PauseListener = PauseListener
    del PauseListener

    _M_echoes.PauseListenerPrx = PauseListenerPrx
    del PauseListenerPrx

# End of module echoes
