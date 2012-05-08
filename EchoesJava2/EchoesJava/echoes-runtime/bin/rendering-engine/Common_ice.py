# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `Common.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()

# Start of module echoes
_M_echoes = Ice.openModule('echoes')
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('_t_StringSeq'):
    _M_echoes._t_StringSeq = IcePy.defineSequence('::echoes::StringSeq', (), IcePy._t_string)

if not _M_echoes.__dict__.has_key('ScreenLocation'):
    _M_echoes.ScreenLocation = Ice.createTempClass()
    class ScreenLocation(object):
        def __init__(self, x=0, y=0):
            self.x = x
            self.y = y

        def __hash__(self):
            _h = 0
            _h = 5 * _h + __builtin__.hash(self.x)
            _h = 5 * _h + __builtin__.hash(self.y)
            return _h % 0x7fffffff

        def __cmp__(self, other):
            if other == None:
                return 1
            if self.x < other.x:
                return -1
            elif self.x > other.x:
                return 1
            if self.y < other.y:
                return -1
            elif self.y > other.y:
                return 1
            return 0

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_ScreenLocation)

        __repr__ = __str__

    _M_echoes._t_ScreenLocation = IcePy.defineStruct('::echoes::ScreenLocation', ScreenLocation, (), (
        ('x', (), IcePy._t_int),
        ('y', (), IcePy._t_int)
    ))

    _M_echoes.ScreenLocation = ScreenLocation
    del ScreenLocation

if not _M_echoes.__dict__.has_key('ScreenRegion'):
    _M_echoes.ScreenRegion = Ice.createTempClass()
    class ScreenRegion(object):

        def __init__(self, val):
            assert(val >= 0 and val < 10)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'ScreenTopLeft'
            elif self.value == 1:
                return 'ScreenTopMiddle'
            elif self.value == 2:
                return 'ScreenTopRight'
            elif self.value == 3:
                return 'ScreenMiddleLeft'
            elif self.value == 4:
                return 'ScreenMiddleMiddle'
            elif self.value == 5:
                return 'ScreenMiddleRight'
            elif self.value == 6:
                return 'ScreenBottomLeft'
            elif self.value == 7:
                return 'ScreenBottomMiddle'
            elif self.value == 8:
                return 'ScreenBottomRight'
            elif self.value == 9:
                return 'ScreenUnknown'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    ScreenRegion.ScreenTopLeft = ScreenRegion(0)
    ScreenRegion.ScreenTopMiddle = ScreenRegion(1)
    ScreenRegion.ScreenTopRight = ScreenRegion(2)
    ScreenRegion.ScreenMiddleLeft = ScreenRegion(3)
    ScreenRegion.ScreenMiddleMiddle = ScreenRegion(4)
    ScreenRegion.ScreenMiddleRight = ScreenRegion(5)
    ScreenRegion.ScreenBottomLeft = ScreenRegion(6)
    ScreenRegion.ScreenBottomMiddle = ScreenRegion(7)
    ScreenRegion.ScreenBottomRight = ScreenRegion(8)
    ScreenRegion.ScreenUnknown = ScreenRegion(9)

    _M_echoes._t_ScreenRegion = IcePy.defineEnum('::echoes::ScreenRegion', ScreenRegion, (), (ScreenRegion.ScreenTopLeft, ScreenRegion.ScreenTopMiddle, ScreenRegion.ScreenTopRight, ScreenRegion.ScreenMiddleLeft, ScreenRegion.ScreenMiddleMiddle, ScreenRegion.ScreenMiddleRight, ScreenRegion.ScreenBottomLeft, ScreenRegion.ScreenBottomMiddle, ScreenRegion.ScreenBottomRight, ScreenRegion.ScreenUnknown))

    _M_echoes.ScreenRegion = ScreenRegion
    del ScreenRegion

if not _M_echoes.__dict__.has_key('FacialExpression'):
    _M_echoes.FacialExpression = Ice.createTempClass()
    class FacialExpression(object):

        def __init__(self, val):
            assert(val >= 0 and val < 2)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'ExpressionSmile'
            elif self.value == 1:
                return 'ExpressionUnknown'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    FacialExpression.ExpressionSmile = FacialExpression(0)
    FacialExpression.ExpressionUnknown = FacialExpression(1)

    _M_echoes._t_FacialExpression = IcePy.defineEnum('::echoes::FacialExpression', FacialExpression, (), (FacialExpression.ExpressionSmile, FacialExpression.ExpressionUnknown))

    _M_echoes.FacialExpression = FacialExpression
    del FacialExpression

if not _M_echoes.__dict__.has_key('_t_Properties'):
    _M_echoes._t_Properties = IcePy.defineDictionary('::echoes::Properties', (), IcePy._t_string, IcePy._t_string)

if not _M_echoes.__dict__.has_key('EchoesObject'):
    _M_echoes.EchoesObject = Ice.createTempClass()
    class EchoesObject(object):
        def __init__(self, name='', props=None):
            self.name = name
            self.props = props

        def __hash__(self):
            _h = 0
            _h = 5 * _h + __builtin__.hash(self.name)
            if self.props:
                for _i0 in self.props:
                    _h = 5 * _h + __builtin__.hash(_i0)
                    _h = 5 * _h + __builtin__.hash(self.props[_i0])
            return _h % 0x7fffffff

        def __cmp__(self, other):
            if other == None:
                return 1
            if self.name < other.name:
                return -1
            elif self.name > other.name:
                return 1
            if self.props < other.props:
                return -1
            elif self.props > other.props:
                return 1
            return 0

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_EchoesObject)

        __repr__ = __str__

    _M_echoes._t_EchoesObject = IcePy.defineStruct('::echoes::EchoesObject', EchoesObject, (), (
        ('name', (), IcePy._t_string),
        ('props', (), _M_echoes._t_Properties)
    ))

    _M_echoes.EchoesObject = EchoesObject
    del EchoesObject

if not _M_echoes.__dict__.has_key('ScertsGoal'):
    _M_echoes.ScertsGoal = Ice.createTempClass()
    class ScertsGoal(object):

        def __init__(self, val):
            assert(val >= 0 and val < 29)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'FollowRemotePoint'
            elif self.value == 1:
                return 'FollowContactPoint'
            elif self.value == 2:
                return 'InitiateNonVerbalBid'
            elif self.value == 3:
                return 'InitiateVerbalBid'
            elif self.value == 4:
                return 'InitiateSocialGame'
            elif self.value == 5:
                return 'NonverballyRespondBid'
            elif self.value == 6:
                return 'VerballyRespondBid'
            elif self.value == 7:
                return 'BriefInteraction'
            elif self.value == 8:
                return 'ExtendedInteraction'
            elif self.value == 9:
                return 'TurnTaking'
            elif self.value == 10:
                return 'SmilesToAgent'
            elif self.value == 11:
                return 'MonitorPartner'
            elif self.value == 12:
                return 'SecureAttention'
            elif self.value == 13:
                return 'ImitateIfElicited'
            elif self.value == 14:
                return 'ImitateSpontaneously'
            elif self.value == 15:
                return 'ImitateAtLaterTime'
            elif self.value == 16:
                return 'NonverballyInitiateJointAttention'
            elif self.value == 17:
                return 'NonverballyRespondJointAttention'
            elif self.value == 18:
                return 'DescribeEmotions'
            elif self.value == 19:
                return 'RespondToEmotions'
            elif self.value == 20:
                return 'LooksToAgent'
            elif self.value == 21:
                return 'ShiftGaze'
            elif self.value == 22:
                return 'LooksToObject'
            elif self.value == 23:
                return 'RequestObject'
            elif self.value == 24:
                return 'ProtestObjectActivity'
            elif self.value == 25:
                return 'VerbalGreeting'
            elif self.value == 26:
                return 'NonVerbalGreeting'
            elif self.value == 27:
                return 'RespondRequestObject'
            elif self.value == 28:
                return 'AnticipateAction'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    ScertsGoal.FollowRemotePoint = ScertsGoal(0)
    ScertsGoal.FollowContactPoint = ScertsGoal(1)
    ScertsGoal.InitiateNonVerbalBid = ScertsGoal(2)
    ScertsGoal.InitiateVerbalBid = ScertsGoal(3)
    ScertsGoal.InitiateSocialGame = ScertsGoal(4)
    ScertsGoal.NonverballyRespondBid = ScertsGoal(5)
    ScertsGoal.VerballyRespondBid = ScertsGoal(6)
    ScertsGoal.BriefInteraction = ScertsGoal(7)
    ScertsGoal.ExtendedInteraction = ScertsGoal(8)
    ScertsGoal.TurnTaking = ScertsGoal(9)
    ScertsGoal.SmilesToAgent = ScertsGoal(10)
    ScertsGoal.MonitorPartner = ScertsGoal(11)
    ScertsGoal.SecureAttention = ScertsGoal(12)
    ScertsGoal.ImitateIfElicited = ScertsGoal(13)
    ScertsGoal.ImitateSpontaneously = ScertsGoal(14)
    ScertsGoal.ImitateAtLaterTime = ScertsGoal(15)
    ScertsGoal.NonverballyInitiateJointAttention = ScertsGoal(16)
    ScertsGoal.NonverballyRespondJointAttention = ScertsGoal(17)
    ScertsGoal.DescribeEmotions = ScertsGoal(18)
    ScertsGoal.RespondToEmotions = ScertsGoal(19)
    ScertsGoal.LooksToAgent = ScertsGoal(20)
    ScertsGoal.ShiftGaze = ScertsGoal(21)
    ScertsGoal.LooksToObject = ScertsGoal(22)
    ScertsGoal.RequestObject = ScertsGoal(23)
    ScertsGoal.ProtestObjectActivity = ScertsGoal(24)
    ScertsGoal.VerbalGreeting = ScertsGoal(25)
    ScertsGoal.NonVerbalGreeting = ScertsGoal(26)
    ScertsGoal.RespondRequestObject = ScertsGoal(27)
    ScertsGoal.AnticipateAction = ScertsGoal(28)

    _M_echoes._t_ScertsGoal = IcePy.defineEnum('::echoes::ScertsGoal', ScertsGoal, (), (ScertsGoal.FollowRemotePoint, ScertsGoal.FollowContactPoint, ScertsGoal.InitiateNonVerbalBid, ScertsGoal.InitiateVerbalBid, ScertsGoal.InitiateSocialGame, ScertsGoal.NonverballyRespondBid, ScertsGoal.VerballyRespondBid, ScertsGoal.BriefInteraction, ScertsGoal.ExtendedInteraction, ScertsGoal.TurnTaking, ScertsGoal.SmilesToAgent, ScertsGoal.MonitorPartner, ScertsGoal.SecureAttention, ScertsGoal.ImitateIfElicited, ScertsGoal.ImitateSpontaneously, ScertsGoal.ImitateAtLaterTime, ScertsGoal.NonverballyInitiateJointAttention, ScertsGoal.NonverballyRespondJointAttention, ScertsGoal.DescribeEmotions, ScertsGoal.RespondToEmotions, ScertsGoal.LooksToAgent, ScertsGoal.ShiftGaze, ScertsGoal.LooksToObject, ScertsGoal.RequestObject, ScertsGoal.ProtestObjectActivity, ScertsGoal.VerbalGreeting, ScertsGoal.NonVerbalGreeting, ScertsGoal.RespondRequestObject, ScertsGoal.AnticipateAction))

    _M_echoes.ScertsGoal = ScertsGoal
    del ScertsGoal

if not _M_echoes.__dict__.has_key('EchoesActivity'):
    _M_echoes.EchoesActivity = Ice.createTempClass()
    class EchoesActivity(object):

        def __init__(self, val):
            assert(val >= 0 and val < 14)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'BubbleActivity'
            elif self.value == 1:
                return 'FlowerPickToBasket'
            elif self.value == 2:
                return 'FlowerTurnToBall'
            elif self.value == 3:
                return 'FlowerGrow'
            elif self.value == 4:
                return 'CloudRain'
            elif self.value == 5:
                return 'PotStackRetrieveObject'
            elif self.value == 6:
                return 'AgentPoke'
            elif self.value == 7:
                return 'Explore'
            elif self.value == 8:
                return 'BallSorting'
            elif self.value == 9:
                return 'BallThrowing'
            elif self.value == 10:
                return 'TickleAndTree'
            elif self.value == 11:
                return 'ExploreWithAgent'
            elif self.value == 12:
                return 'BallThrowingContingent'
            elif self.value == 13:
                return 'FlowerTurnToBallContingent'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    EchoesActivity.BubbleActivity = EchoesActivity(0)
    EchoesActivity.FlowerPickToBasket = EchoesActivity(1)
    EchoesActivity.FlowerTurnToBall = EchoesActivity(2)
    EchoesActivity.FlowerGrow = EchoesActivity(3)
    EchoesActivity.CloudRain = EchoesActivity(4)
    EchoesActivity.PotStackRetrieveObject = EchoesActivity(5)
    EchoesActivity.AgentPoke = EchoesActivity(6)
    EchoesActivity.Explore = EchoesActivity(7)
    EchoesActivity.BallSorting = EchoesActivity(8)
    EchoesActivity.BallThrowing = EchoesActivity(9)
    EchoesActivity.TickleAndTree = EchoesActivity(10)
    EchoesActivity.ExploreWithAgent = EchoesActivity(11)
    EchoesActivity.BallThrowingContingent = EchoesActivity(12)
    EchoesActivity.FlowerTurnToBallContingent = EchoesActivity(13)

    _M_echoes._t_EchoesActivity = IcePy.defineEnum('::echoes::EchoesActivity', EchoesActivity, (), (EchoesActivity.BubbleActivity, EchoesActivity.FlowerPickToBasket, EchoesActivity.FlowerTurnToBall, EchoesActivity.FlowerGrow, EchoesActivity.CloudRain, EchoesActivity.PotStackRetrieveObject, EchoesActivity.AgentPoke, EchoesActivity.Explore, EchoesActivity.BallSorting, EchoesActivity.BallThrowing, EchoesActivity.TickleAndTree, EchoesActivity.ExploreWithAgent, EchoesActivity.BallThrowingContingent, EchoesActivity.FlowerTurnToBallContingent))

    _M_echoes.EchoesActivity = EchoesActivity
    del EchoesActivity

if not _M_echoes.__dict__.has_key('EchoesObjectType'):
    _M_echoes.EchoesObjectType = Ice.createTempClass()
    class EchoesObjectType(object):

        def __init__(self, val):
            assert(val >= 0 and val < 12)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'Shed'
            elif self.value == 1:
                return 'LifeTree'
            elif self.value == 2:
                return 'IntroBubble'
            elif self.value == 3:
                return 'Bubble'
            elif self.value == 4:
                return 'Basket'
            elif self.value == 5:
                return 'Flower'
            elif self.value == 6:
                return 'Pot'
            elif self.value == 7:
                return 'Cloud'
            elif self.value == 8:
                return 'Pond'
            elif self.value == 9:
                return 'MagicLeaves'
            elif self.value == 10:
                return 'Ball'
            elif self.value == 11:
                return 'Container'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    EchoesObjectType.Shed = EchoesObjectType(0)
    EchoesObjectType.LifeTree = EchoesObjectType(1)
    EchoesObjectType.IntroBubble = EchoesObjectType(2)
    EchoesObjectType.Bubble = EchoesObjectType(3)
    EchoesObjectType.Basket = EchoesObjectType(4)
    EchoesObjectType.Flower = EchoesObjectType(5)
    EchoesObjectType.Pot = EchoesObjectType(6)
    EchoesObjectType.Cloud = EchoesObjectType(7)
    EchoesObjectType.Pond = EchoesObjectType(8)
    EchoesObjectType.MagicLeaves = EchoesObjectType(9)
    EchoesObjectType.Ball = EchoesObjectType(10)
    EchoesObjectType.Container = EchoesObjectType(11)

    _M_echoes._t_EchoesObjectType = IcePy.defineEnum('::echoes::EchoesObjectType', EchoesObjectType, (), (EchoesObjectType.Shed, EchoesObjectType.LifeTree, EchoesObjectType.IntroBubble, EchoesObjectType.Bubble, EchoesObjectType.Basket, EchoesObjectType.Flower, EchoesObjectType.Pot, EchoesObjectType.Cloud, EchoesObjectType.Pond, EchoesObjectType.MagicLeaves, EchoesObjectType.Ball, EchoesObjectType.Container))

    _M_echoes.EchoesObjectType = EchoesObjectType
    del EchoesObjectType

if not _M_echoes.__dict__.has_key('EchoesScene'):
    _M_echoes.EchoesScene = Ice.createTempClass()
    class EchoesScene(object):

        def __init__(self, val):
            assert(val >= 0 and val < 6)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'NoScene'
            elif self.value == 1:
                return 'Intro'
            elif self.value == 2:
                return 'Bubbles'
            elif self.value == 3:
                return 'Garden'
            elif self.value == 4:
                return 'GardenTask'
            elif self.value == 5:
                return 'GardenSocialGame'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    EchoesScene.NoScene = EchoesScene(0)
    EchoesScene.Intro = EchoesScene(1)
    EchoesScene.Bubbles = EchoesScene(2)
    EchoesScene.Garden = EchoesScene(3)
    EchoesScene.GardenTask = EchoesScene(4)
    EchoesScene.GardenSocialGame = EchoesScene(5)

    _M_echoes._t_EchoesScene = IcePy.defineEnum('::echoes::EchoesScene', EchoesScene, (), (EchoesScene.NoScene, EchoesScene.Intro, EchoesScene.Bubbles, EchoesScene.Garden, EchoesScene.GardenTask, EchoesScene.GardenSocialGame))

    _M_echoes.EchoesScene = EchoesScene
    del EchoesScene

# End of module echoes
