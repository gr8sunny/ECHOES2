# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `ActionEngine.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('ActionEngine'):
    _M_echoes.ActionEngine = Ice.createTempClass()
    class ActionEngine(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.ActionEngine:
                raise RuntimeError('echoes.ActionEngine is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::ActionEngine')

        def ice_id(self, current=None):
            return '::echoes::ActionEngine'

        def ice_staticId():
            return '::echoes::ActionEngine'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def setChildname(self, name, current=None):
        # def setGoal(self, goal, current=None):
        # def resetGoal(self, goal, current=None):
        # def setChosenActivity(self, activity, current=None):
        # def setReactToEvent(self, look, event, objId, shareWithChild, current=None):
        # def setBidType(self, bid, current=None):
        # def setBidPurpose(self, purpose, current=None):
        # def setBidRepeat(self, bidRepeat, current=None):
        # def setTarget(self, objectId, current=None):
        # def resetGoalSuccessConditions(self, current=None):
        # def resetAgentPlan(self, current=None):
        # def activityEnded(self, current=None):
        # def getTargetObject(self, type, current=None):
        # def cancelAllGoals(self, current=None):
        # def setObjectFocus(self, objId, purpose, current=None):
        # def setReward(self, ballColour, current=None):
        # def setBallSortingTargets(self, ballId, containerId, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_ActionEngine)

        __repr__ = __str__

    _M_echoes.ActionEnginePrx = Ice.createTempClass()
    class ActionEnginePrx(Ice.ObjectPrx):

        def setChildname(self, name, _ctx=None):
            return _M_echoes.ActionEngine._op_setChildname.invoke(self, ((name, ), _ctx))

        def setGoal(self, goal, _ctx=None):
            return _M_echoes.ActionEngine._op_setGoal.invoke(self, ((goal, ), _ctx))

        def resetGoal(self, goal, _ctx=None):
            return _M_echoes.ActionEngine._op_resetGoal.invoke(self, ((goal, ), _ctx))

        def setChosenActivity(self, activity, _ctx=None):
            return _M_echoes.ActionEngine._op_setChosenActivity.invoke(self, ((activity, ), _ctx))

        def setReactToEvent(self, look, event, objId, shareWithChild, _ctx=None):
            return _M_echoes.ActionEngine._op_setReactToEvent.invoke(self, ((look, event, objId, shareWithChild), _ctx))

        def setBidType(self, bid, _ctx=None):
            return _M_echoes.ActionEngine._op_setBidType.invoke(self, ((bid, ), _ctx))

        def setBidPurpose(self, purpose, _ctx=None):
            return _M_echoes.ActionEngine._op_setBidPurpose.invoke(self, ((purpose, ), _ctx))

        def setBidRepeat(self, bidRepeat, _ctx=None):
            return _M_echoes.ActionEngine._op_setBidRepeat.invoke(self, ((bidRepeat, ), _ctx))

        def setTarget(self, objectId, _ctx=None):
            return _M_echoes.ActionEngine._op_setTarget.invoke(self, ((objectId, ), _ctx))

        def resetGoalSuccessConditions(self, _ctx=None):
            return _M_echoes.ActionEngine._op_resetGoalSuccessConditions.invoke(self, ((), _ctx))

        def resetAgentPlan(self, _ctx=None):
            return _M_echoes.ActionEngine._op_resetAgentPlan.invoke(self, ((), _ctx))

        def activityEnded(self, _ctx=None):
            return _M_echoes.ActionEngine._op_activityEnded.invoke(self, ((), _ctx))

        def getTargetObject(self, type, _ctx=None):
            return _M_echoes.ActionEngine._op_getTargetObject.invoke(self, ((type, ), _ctx))

        def cancelAllGoals(self, _ctx=None):
            return _M_echoes.ActionEngine._op_cancelAllGoals.invoke(self, ((), _ctx))

        def setObjectFocus(self, objId, purpose, _ctx=None):
            return _M_echoes.ActionEngine._op_setObjectFocus.invoke(self, ((objId, purpose), _ctx))

        def setReward(self, ballColour, _ctx=None):
            return _M_echoes.ActionEngine._op_setReward.invoke(self, ((ballColour, ), _ctx))

        def setBallSortingTargets(self, ballId, containerId, _ctx=None):
            return _M_echoes.ActionEngine._op_setBallSortingTargets.invoke(self, ((ballId, containerId), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.ActionEnginePrx.ice_checkedCast(proxy, '::echoes::ActionEngine', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.ActionEnginePrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_ActionEnginePrx = IcePy.defineProxy('::echoes::ActionEngine', ActionEnginePrx)

    _M_echoes._t_ActionEngine = IcePy.defineClass('::echoes::ActionEngine', ActionEngine, (), True, None, (), ())
    ActionEngine.ice_type = _M_echoes._t_ActionEngine

    ActionEngine._op_setChildname = IcePy.Operation('setChildname', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_setGoal = IcePy.Operation('setGoal', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_resetGoal = IcePy.Operation('resetGoal', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_setChosenActivity = IcePy.Operation('setChosenActivity', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_setReactToEvent = IcePy.Operation('setReactToEvent', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_bool), ((), IcePy._t_string), ((), IcePy._t_string), ((), IcePy._t_bool)), (), None, ())
    ActionEngine._op_setBidType = IcePy.Operation('setBidType', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_setBidPurpose = IcePy.Operation('setBidPurpose', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_setBidRepeat = IcePy.Operation('setBidRepeat', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_setTarget = IcePy.Operation('setTarget', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_resetGoalSuccessConditions = IcePy.Operation('resetGoalSuccessConditions', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), None, ())
    ActionEngine._op_resetAgentPlan = IcePy.Operation('resetAgentPlan', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), None, ())
    ActionEngine._op_activityEnded = IcePy.Operation('activityEnded', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), None, ())
    ActionEngine._op_getTargetObject = IcePy.Operation('getTargetObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesObjectType),), (), IcePy._t_string, ())
    ActionEngine._op_cancelAllGoals = IcePy.Operation('cancelAllGoals', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), None, ())
    ActionEngine._op_setObjectFocus = IcePy.Operation('setObjectFocus', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string)), (), None, ())
    ActionEngine._op_setReward = IcePy.Operation('setReward', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ActionEngine._op_setBallSortingTargets = IcePy.Operation('setBallSortingTargets', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), IcePy._t_string)), (), None, ())

    _M_echoes.ActionEngine = ActionEngine
    del ActionEngine

    _M_echoes.ActionEnginePrx = ActionEnginePrx
    del ActionEnginePrx

# End of module echoes
