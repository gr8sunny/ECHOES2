# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `HeadTracker.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()

# Start of module echoes
_M_echoes = Ice.openModule('echoes')
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('HeadTrackerMode'):
    _M_echoes.HeadTrackerMode = Ice.createTempClass()
    class HeadTrackerMode(object):

        def __init__(self, val):
            assert(val >= 0 and val < 2)
            self.value = val

        def __str__(self):
            if self.value == 0:
                return 'ModelMode'
            elif self.value == 1:
                return 'TrackMode'
            return None

        __repr__ = __str__

        def __hash__(self):
            return self.value

        def __cmp__(self, other):
            return cmp(self.value, other.value)

    HeadTrackerMode.ModelMode = HeadTrackerMode(0)
    HeadTrackerMode.TrackMode = HeadTrackerMode(1)

    _M_echoes._t_HeadTrackerMode = IcePy.defineEnum('::echoes::HeadTrackerMode', HeadTrackerMode, (), (HeadTrackerMode.ModelMode, HeadTrackerMode.TrackMode))

    _M_echoes.HeadTrackerMode = HeadTrackerMode
    del HeadTrackerMode

if not _M_echoes.__dict__.has_key('HeadTracker'):
    _M_echoes.HeadTracker = Ice.createTempClass()
    class HeadTracker(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.HeadTracker:
                raise RuntimeError('echoes.HeadTracker is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::HeadTracker')

        def ice_id(self, current=None):
            return '::echoes::HeadTracker'

        def ice_staticId():
            return '::echoes::HeadTracker'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def buildHeadModel_async(self, _cb, current=None):
        # def skipHeadModel_async(self, _cb, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_HeadTracker)

        __repr__ = __str__

    _M_echoes.HeadTrackerPrx = Ice.createTempClass()
    class HeadTrackerPrx(Ice.ObjectPrx):

        def buildHeadModel(self, _ctx=None):
            return _M_echoes.HeadTracker._op_buildHeadModel.invoke(self, ((), _ctx))

        def skipHeadModel(self, _ctx=None):
            return _M_echoes.HeadTracker._op_skipHeadModel.invoke(self, ((), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.HeadTrackerPrx.ice_checkedCast(proxy, '::echoes::HeadTracker', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.HeadTrackerPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_HeadTrackerPrx = IcePy.defineProxy('::echoes::HeadTracker', HeadTrackerPrx)

    _M_echoes._t_HeadTracker = IcePy.defineClass('::echoes::HeadTracker', HeadTracker, (), True, None, (), ())
    HeadTracker.ice_type = _M_echoes._t_HeadTracker

    HeadTracker._op_buildHeadModel = IcePy.Operation('buildHeadModel', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (), (), IcePy._t_bool, ())
    HeadTracker._op_skipHeadModel = IcePy.Operation('skipHeadModel', Ice.OperationMode.Normal, Ice.OperationMode.Normal, True, (), (), (), None, ())

    _M_echoes.HeadTracker = HeadTracker
    del HeadTracker

    _M_echoes.HeadTrackerPrx = HeadTrackerPrx
    del HeadTrackerPrx

# End of module echoes
