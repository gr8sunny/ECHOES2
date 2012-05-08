# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `TouchListener.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()

# Start of module echoes
_M_echoes = Ice.openModule('echoes')
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('TouchListener'):
    _M_echoes.TouchListener = Ice.createTempClass()
    class TouchListener(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.TouchListener:
                raise RuntimeError('echoes.TouchListener is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::TouchListener')

        def ice_id(self, current=None):
            return '::echoes::TouchListener'

        def ice_staticId():
            return '::echoes::TouchListener'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def click(self, x, y, width, height, current=None):
        # def pointDown(self, id, x, y, width, height, current=None):
        # def pointMoved(self, id, newX, newY, newWidth, newHeight, current=None):
        # def pointUp(self, id, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_TouchListener)

        __repr__ = __str__

    _M_echoes.TouchListenerPrx = Ice.createTempClass()
    class TouchListenerPrx(Ice.ObjectPrx):

        def click(self, x, y, width, height, _ctx=None):
            return _M_echoes.TouchListener._op_click.invoke(self, ((x, y, width, height), _ctx))

        def pointDown(self, id, x, y, width, height, _ctx=None):
            return _M_echoes.TouchListener._op_pointDown.invoke(self, ((id, x, y, width, height), _ctx))

        def pointMoved(self, id, newX, newY, newWidth, newHeight, _ctx=None):
            return _M_echoes.TouchListener._op_pointMoved.invoke(self, ((id, newX, newY, newWidth, newHeight), _ctx))

        def pointUp(self, id, _ctx=None):
            return _M_echoes.TouchListener._op_pointUp.invoke(self, ((id, ), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.TouchListenerPrx.ice_checkedCast(proxy, '::echoes::TouchListener', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.TouchListenerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_TouchListenerPrx = IcePy.defineProxy('::echoes::TouchListener', TouchListenerPrx)

    _M_echoes._t_TouchListener = IcePy.defineClass('::echoes::TouchListener', TouchListener, (), True, None, (), ())
    TouchListener.ice_type = _M_echoes._t_TouchListener

    TouchListener._op_click = IcePy.Operation('click', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int), ((), IcePy._t_int), ((), IcePy._t_int), ((), IcePy._t_int)), (), None, ())
    TouchListener._op_pointDown = IcePy.Operation('pointDown', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int), ((), IcePy._t_int), ((), IcePy._t_int), ((), IcePy._t_int), ((), IcePy._t_int)), (), None, ())
    TouchListener._op_pointMoved = IcePy.Operation('pointMoved', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int), ((), IcePy._t_int), ((), IcePy._t_int), ((), IcePy._t_int), ((), IcePy._t_int)), (), None, ())
    TouchListener._op_pointUp = IcePy.Operation('pointUp', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int),), (), None, ())

    _M_echoes.TouchListener = TouchListener
    del TouchListener

    _M_echoes.TouchListenerPrx = TouchListenerPrx
    del TouchListenerPrx

# End of module echoes
