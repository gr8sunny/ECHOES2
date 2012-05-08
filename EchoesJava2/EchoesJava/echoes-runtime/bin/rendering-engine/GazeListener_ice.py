# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `GazeListener.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('GazeListener'):
    _M_echoes.GazeListener = Ice.createTempClass()
    class GazeListener(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.GazeListener:
                raise RuntimeError('echoes.GazeListener is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::GazeListener')

        def ice_id(self, current=None):
            return '::echoes::GazeListener'

        def ice_staticId():
            return '::echoes::GazeListener'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def userGaze(self, loc, current=None):
        # def userVisibilityChanged(self, visible, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_GazeListener)

        __repr__ = __str__

    _M_echoes.GazeListenerPrx = Ice.createTempClass()
    class GazeListenerPrx(Ice.ObjectPrx):

        def userGaze(self, loc, _ctx=None):
            return _M_echoes.GazeListener._op_userGaze.invoke(self, ((loc, ), _ctx))

        def userVisibilityChanged(self, visible, _ctx=None):
            return _M_echoes.GazeListener._op_userVisibilityChanged.invoke(self, ((visible, ), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.GazeListenerPrx.ice_checkedCast(proxy, '::echoes::GazeListener', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.GazeListenerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_GazeListenerPrx = IcePy.defineProxy('::echoes::GazeListener', GazeListenerPrx)

    _M_echoes._t_GazeListener = IcePy.defineClass('::echoes::GazeListener', GazeListener, (), True, None, (), ())
    GazeListener.ice_type = _M_echoes._t_GazeListener

    GazeListener._op_userGaze = IcePy.Operation('userGaze', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_ScreenLocation),), (), None, ())
    GazeListener._op_userVisibilityChanged = IcePy.Operation('userVisibilityChanged', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_bool),), (), None, ())

    _M_echoes.GazeListener = GazeListener
    del GazeListener

    _M_echoes.GazeListenerPrx = GazeListenerPrx
    del GazeListenerPrx

# End of module echoes
