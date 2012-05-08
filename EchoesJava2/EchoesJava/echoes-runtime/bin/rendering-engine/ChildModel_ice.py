# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `ChildModel.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('Engagement'):
    _M_echoes.Engagement = Ice.createTempClass()
    class Engagement(object):

        def __init__(self, val):
            assert(val >= 0 and val < 6)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'DisengTotal'
            elif self.value == 1:
                return 'DisengMinus'
            elif self.value == 2:
                return 'DisengPlus'
            elif self.value == 3:
                return 'Eng'
            elif self.value == 4:
                return 'EngPlus'
            elif self.value == 5:
                return 'EngPlusPlus'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    Engagement.DisengTotal = Engagement(0)
    Engagement.DisengMinus = Engagement(1)
    Engagement.DisengPlus = Engagement(2)
    Engagement.Eng = Engagement(3)
    Engagement.EngPlus = Engagement(4)
    Engagement.EngPlusPlus = Engagement(5)

    _M_echoes._t_Engagement = IcePy.defineEnum('::echoes::Engagement', Engagement, (), (Engagement.DisengTotal, Engagement.DisengMinus, Engagement.DisengPlus, Engagement.Eng, Engagement.EngPlus, Engagement.EngPlusPlus))

    _M_echoes.Engagement = Engagement
    del Engagement

if not _M_echoes.__dict__.has_key('ChildModel'):
    _M_echoes.ChildModel = Ice.createTempClass()
    class ChildModel(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.ChildModel:
                raise RuntimeError('echoes.ChildModel is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::ChildModel')

        def ice_id(self, current=None):
            return '::echoes::ChildModel'

        def ice_staticId():
            return '::echoes::ChildModel'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def getChildNameForFile(self, fileName, current=None):
        # def createModel(self, current=None):
        # def loadModel(self, fileName, current=None):
        # def saveModel(self, current=None):
        # def listModels(self, current=None):
        # def getGoalLevelOfDirection(self, goal, current=None):
        # def setTargetObject(self, objId, current=None):
        # def getEngagement(self, current=None):
        # def getChildName(self, current=None):
        # def setChildName(self, name, current=None):
        # def getAge(self, current=None):
        # def setAge(self, age, current=None):
        # def getSchool(self, current=None):
        # def setSchool(self, school, current=None):
        # def isOpenToAgent(self, current=None):
        # def setOpenToAgent(self, open, current=None):
        # def displayScore(self, current=None):
        # def setDisplayScore(self, display, current=None):
        # def getBubbleComplexity(self, current=None):
        # def setBubbleComplexity(self, complexity, current=None):
        # def getNumRepetitions(self, current=None):
        # def setNumRepetitions(self, numRepetitions, current=None):
        # def getOverallLevelOfDirection(self, current=None):
        # def setOverallLevelOfDirection(self, level, current=None):
        # def getAbility(self, goal, current=None):
        # def setAbility(self, goal, ability, current=None):
        # def getActivityValue(self, activity, current=None):
        # def setActivityValue(self, activity, value, current=None):
        # def getObjectValue(self, echoesObject, current=None):
        # def setObjectValue(self, objectType, value, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_ChildModel)

        __repr__ = __str__

    _M_echoes.ChildModelPrx = Ice.createTempClass()
    class ChildModelPrx(Ice.ObjectPrx):

        def getChildNameForFile(self, fileName, _ctx=None):
            return _M_echoes.ChildModel._op_getChildNameForFile.invoke(self, ((fileName, ), _ctx))

        def createModel(self, _ctx=None):
            return _M_echoes.ChildModel._op_createModel.invoke(self, ((), _ctx))

        def loadModel(self, fileName, _ctx=None):
            return _M_echoes.ChildModel._op_loadModel.invoke(self, ((fileName, ), _ctx))

        def saveModel(self, _ctx=None):
            return _M_echoes.ChildModel._op_saveModel.invoke(self, ((), _ctx))

        def listModels(self, _ctx=None):
            return _M_echoes.ChildModel._op_listModels.invoke(self, ((), _ctx))

        def getGoalLevelOfDirection(self, goal, _ctx=None):
            return _M_echoes.ChildModel._op_getGoalLevelOfDirection.invoke(self, ((goal, ), _ctx))

        def setTargetObject(self, objId, _ctx=None):
            return _M_echoes.ChildModel._op_setTargetObject.invoke(self, ((objId, ), _ctx))

        def getEngagement(self, _ctx=None):
            return _M_echoes.ChildModel._op_getEngagement.invoke(self, ((), _ctx))

        def getChildName(self, _ctx=None):
            return _M_echoes.ChildModel._op_getChildName.invoke(self, ((), _ctx))

        def setChildName(self, name, _ctx=None):
            return _M_echoes.ChildModel._op_setChildName.invoke(self, ((name, ), _ctx))

        def getAge(self, _ctx=None):
            return _M_echoes.ChildModel._op_getAge.invoke(self, ((), _ctx))

        def setAge(self, age, _ctx=None):
            return _M_echoes.ChildModel._op_setAge.invoke(self, ((age, ), _ctx))

        def getSchool(self, _ctx=None):
            return _M_echoes.ChildModel._op_getSchool.invoke(self, ((), _ctx))

        def setSchool(self, school, _ctx=None):
            return _M_echoes.ChildModel._op_setSchool.invoke(self, ((school, ), _ctx))

        def isOpenToAgent(self, _ctx=None):
            return _M_echoes.ChildModel._op_isOpenToAgent.invoke(self, ((), _ctx))

        def setOpenToAgent(self, open, _ctx=None):
            return _M_echoes.ChildModel._op_setOpenToAgent.invoke(self, ((open, ), _ctx))

        def displayScore(self, _ctx=None):
            return _M_echoes.ChildModel._op_displayScore.invoke(self, ((), _ctx))

        def setDisplayScore(self, display, _ctx=None):
            return _M_echoes.ChildModel._op_setDisplayScore.invoke(self, ((display, ), _ctx))

        def getBubbleComplexity(self, _ctx=None):
            return _M_echoes.ChildModel._op_getBubbleComplexity.invoke(self, ((), _ctx))

        def setBubbleComplexity(self, complexity, _ctx=None):
            return _M_echoes.ChildModel._op_setBubbleComplexity.invoke(self, ((complexity, ), _ctx))

        def getNumRepetitions(self, _ctx=None):
            return _M_echoes.ChildModel._op_getNumRepetitions.invoke(self, ((), _ctx))

        def setNumRepetitions(self, numRepetitions, _ctx=None):
            return _M_echoes.ChildModel._op_setNumRepetitions.invoke(self, ((numRepetitions, ), _ctx))

        def getOverallLevelOfDirection(self, _ctx=None):
            return _M_echoes.ChildModel._op_getOverallLevelOfDirection.invoke(self, ((), _ctx))

        def setOverallLevelOfDirection(self, level, _ctx=None):
            return _M_echoes.ChildModel._op_setOverallLevelOfDirection.invoke(self, ((level, ), _ctx))

        def getAbility(self, goal, _ctx=None):
            return _M_echoes.ChildModel._op_getAbility.invoke(self, ((goal, ), _ctx))

        def setAbility(self, goal, ability, _ctx=None):
            return _M_echoes.ChildModel._op_setAbility.invoke(self, ((goal, ability), _ctx))

        def getActivityValue(self, activity, _ctx=None):
            return _M_echoes.ChildModel._op_getActivityValue.invoke(self, ((activity, ), _ctx))

        def setActivityValue(self, activity, value, _ctx=None):
            return _M_echoes.ChildModel._op_setActivityValue.invoke(self, ((activity, value), _ctx))

        def getObjectValue(self, echoesObject, _ctx=None):
            return _M_echoes.ChildModel._op_getObjectValue.invoke(self, ((echoesObject, ), _ctx))

        def setObjectValue(self, objectType, value, _ctx=None):
            return _M_echoes.ChildModel._op_setObjectValue.invoke(self, ((objectType, value), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.ChildModelPrx.ice_checkedCast(proxy, '::echoes::ChildModel', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.ChildModelPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_ChildModelPrx = IcePy.defineProxy('::echoes::ChildModel', ChildModelPrx)

    _M_echoes._t_ChildModel = IcePy.defineClass('::echoes::ChildModel', ChildModel, (), True, None, (), ())
    ChildModel.ice_type = _M_echoes._t_ChildModel

    ChildModel._op_getChildNameForFile = IcePy.Operation('getChildNameForFile', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), IcePy._t_string, ())
    ChildModel._op_createModel = IcePy.Operation('createModel', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), None, ())
    ChildModel._op_loadModel = IcePy.Operation('loadModel', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ChildModel._op_saveModel = IcePy.Operation('saveModel', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), None, ())
    ChildModel._op_listModels = IcePy.Operation('listModels', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), _M_echoes._t_StringSeq, ())
    ChildModel._op_getGoalLevelOfDirection = IcePy.Operation('getGoalLevelOfDirection', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_ScertsGoal),), (), IcePy._t_int, ())
    ChildModel._op_setTargetObject = IcePy.Operation('setTargetObject', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ChildModel._op_getEngagement = IcePy.Operation('getEngagement', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), _M_echoes._t_Engagement, ())
    ChildModel._op_getChildName = IcePy.Operation('getChildName', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_string, ())
    ChildModel._op_setChildName = IcePy.Operation('setChildName', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ChildModel._op_getAge = IcePy.Operation('getAge', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_int, ())
    ChildModel._op_setAge = IcePy.Operation('setAge', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int),), (), None, ())
    ChildModel._op_getSchool = IcePy.Operation('getSchool', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_string, ())
    ChildModel._op_setSchool = IcePy.Operation('setSchool', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    ChildModel._op_isOpenToAgent = IcePy.Operation('isOpenToAgent', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_bool, ())
    ChildModel._op_setOpenToAgent = IcePy.Operation('setOpenToAgent', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_bool),), (), None, ())
    ChildModel._op_displayScore = IcePy.Operation('displayScore', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_bool, ())
    ChildModel._op_setDisplayScore = IcePy.Operation('setDisplayScore', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_bool),), (), None, ())
    ChildModel._op_getBubbleComplexity = IcePy.Operation('getBubbleComplexity', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_int, ())
    ChildModel._op_setBubbleComplexity = IcePy.Operation('setBubbleComplexity', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int),), (), None, ())
    ChildModel._op_getNumRepetitions = IcePy.Operation('getNumRepetitions', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_int, ())
    ChildModel._op_setNumRepetitions = IcePy.Operation('setNumRepetitions', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int),), (), None, ())
    ChildModel._op_getOverallLevelOfDirection = IcePy.Operation('getOverallLevelOfDirection', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), IcePy._t_int, ())
    ChildModel._op_setOverallLevelOfDirection = IcePy.Operation('setOverallLevelOfDirection', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int),), (), None, ())
    ChildModel._op_getAbility = IcePy.Operation('getAbility', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_ScertsGoal),), (), IcePy._t_int, ())
    ChildModel._op_setAbility = IcePy.Operation('setAbility', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_ScertsGoal), ((), IcePy._t_int)), (), None, ())
    ChildModel._op_getActivityValue = IcePy.Operation('getActivityValue', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesActivity),), (), IcePy._t_int, ())
    ChildModel._op_setActivityValue = IcePy.Operation('setActivityValue', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesActivity), ((), IcePy._t_int)), (), None, ())
    ChildModel._op_getObjectValue = IcePy.Operation('getObjectValue', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesObjectType),), (), IcePy._t_int, ())
    ChildModel._op_setObjectValue = IcePy.Operation('setObjectValue', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), _M_echoes._t_EchoesObjectType), ((), IcePy._t_int)), (), None, ())

    _M_echoes.ChildModel = ChildModel
    del ChildModel

    _M_echoes.ChildModelPrx = ChildModelPrx
    del ChildModelPrx

# End of module echoes
