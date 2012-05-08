# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `EventListener.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('UserActionType'):
    _M_echoes.UserActionType = Ice.createTempClass()
    class UserActionType(object):

        def __init__(self, val):
            assert(val >= 0 and val < 8)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'UserRespondedToBid'
            elif self.value == 1:
                return 'UserActivityRelevantAction'
            elif self.value == 2:
                return 'UserGaveRequestedObject'
            elif self.value == 3:
                return 'UserGaveUnrequestedObject'
            elif self.value == 4:
                return 'UserUnrelatedAction'
            elif self.value == 5:
                return 'UserNoAction'
            elif self.value == 6:
                return 'UserInitiated'
            elif self.value == 7:
                return 'UserTouchedAgent'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    UserActionType.UserRespondedToBid = UserActionType(0)
    UserActionType.UserActivityRelevantAction = UserActionType(1)
    UserActionType.UserGaveRequestedObject = UserActionType(2)
    UserActionType.UserGaveUnrequestedObject = UserActionType(3)
    UserActionType.UserUnrelatedAction = UserActionType(4)
    UserActionType.UserNoAction = UserActionType(5)
    UserActionType.UserInitiated = UserActionType(6)
    UserActionType.UserTouchedAgent = UserActionType(7)

    _M_echoes._t_UserActionType = IcePy.defineEnum('::echoes::UserActionType', UserActionType, (), (UserActionType.UserRespondedToBid, UserActionType.UserActivityRelevantAction, UserActionType.UserGaveRequestedObject, UserActionType.UserGaveUnrequestedObject, UserActionType.UserUnrelatedAction, UserActionType.UserNoAction, UserActionType.UserInitiated, UserActionType.UserTouchedAgent))

    _M_echoes.UserActionType = UserActionType
    del UserActionType

if not _M_echoes.__dict__.has_key('EventListener'):
    _M_echoes.EventListener = Ice.createTempClass()
    class EventListener(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.EventListener:
                raise RuntimeError('echoes.EventListener is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::EventListener')

        def ice_id(self, current=None):
            return '::echoes::EventListener'

        def ice_staticId():
            return '::echoes::EventListener'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def userGazeEvent(self, details, msec, current=None):
        # def userTouchEvent(self, objId, current=None):
        # def userAction(self, action, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_EventListener)

        __repr__ = __str__

    _M_echoes.EventListenerPrx = Ice.createTempClass()
    class EventListenerPrx(Ice.ObjectPrx):

        def userGazeEvent(self, details, msec, _ctx=None):
            return _M_echoes.EventListener._op_userGazeEvent.invoke(self, ((details, msec), _ctx))

        def userTouchEvent(self, objId, _ctx=None):
            return _M_echoes.EventListener._op_userTouchEvent.invoke(self, ((objId, ), _ctx))

        def userAction(self, action, _ctx=None):
            return _M_echoes.EventListener._op_userAction.invoke(self, ((action, ), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.EventListenerPrx.ice_checkedCast(proxy, '::echoes::EventListener', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.EventListenerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_EventListenerPrx = IcePy.defineProxy('::echoes::EventListener', EventListenerPrx)

    _M_echoes._t_EventListener = IcePy.defineClass('::echoes::EventListener', EventListener, (), True, None, (), ())
    EventListener.ice_type = _M_echoes._t_EventListener

    EventListener._op_userGazeEvent = IcePy.Operation('userGazeEvent', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_long)), (), None, ())
    EventListener._op_userTouchEvent = IcePy.Operation('userTouchEvent', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    EventListener._op_userAction = IcePy.Operation('userAction', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_UserActionType),), (), None, ())

    _M_echoes.EventListener = EventListener
    del EventListener

    _M_echoes.EventListenerPrx = EventListenerPrx
    del EventListenerPrx

# End of module echoes
