# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `UserHeadListener.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('UserHeadListener'):
    _M_echoes.UserHeadListener = Ice.createTempClass()
    class UserHeadListener(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.UserHeadListener:
                raise RuntimeError('echoes.UserHeadListener is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::UserHeadListener')

        def ice_id(self, current=None):
            return '::echoes::UserHeadListener'

        def ice_staticId():
            return '::echoes::UserHeadListener'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def faceSeen(self, faceLeft, faceRight, faceMiddle, current=None):
        # def gaze(self, region, current=None):
        # def userExpression(self, expression, current=None):
        # def userLocation(self, x, y, z, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_UserHeadListener)

        __repr__ = __str__

    _M_echoes.UserHeadListenerPrx = Ice.createTempClass()
    class UserHeadListenerPrx(Ice.ObjectPrx):

        def faceSeen(self, faceLeft, faceRight, faceMiddle, _ctx=None):
            return _M_echoes.UserHeadListener._op_faceSeen.invoke(self, ((faceLeft, faceRight, faceMiddle), _ctx))

        def gaze(self, region, _ctx=None):
            return _M_echoes.UserHeadListener._op_gaze.invoke(self, ((region, ), _ctx))

        def userExpression(self, expression, _ctx=None):
            return _M_echoes.UserHeadListener._op_userExpression.invoke(self, ((expression, ), _ctx))

        def userLocation(self, x, y, z, _ctx=None):
            return _M_echoes.UserHeadListener._op_userLocation.invoke(self, ((x, y, z), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.UserHeadListenerPrx.ice_checkedCast(proxy, '::echoes::UserHeadListener', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.UserHeadListenerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_UserHeadListenerPrx = IcePy.defineProxy('::echoes::UserHeadListener', UserHeadListenerPrx)

    _M_echoes._t_UserHeadListener = IcePy.defineClass('::echoes::UserHeadListener', UserHeadListener, (), True, None, (), ())
    UserHeadListener.ice_type = _M_echoes._t_UserHeadListener

    UserHeadListener._op_faceSeen = IcePy.Operation('faceSeen', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_bool), ((), IcePy._t_bool), ((), IcePy._t_bool)), (), None, ())
    UserHeadListener._op_gaze = IcePy.Operation('gaze', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_ScreenRegion),), (), None, ())
    UserHeadListener._op_userExpression = IcePy.Operation('userExpression', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_FacialExpression),), (), None, ())
    UserHeadListener._op_userLocation = IcePy.Operation('userLocation', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_double), ((), IcePy._t_double), ((), IcePy._t_double)), (), None, ())

    _M_echoes.UserHeadListener = UserHeadListener
    del UserHeadListener

    _M_echoes.UserHeadListenerPrx = UserHeadListenerPrx
    del UserHeadListenerPrx

# End of module echoes
