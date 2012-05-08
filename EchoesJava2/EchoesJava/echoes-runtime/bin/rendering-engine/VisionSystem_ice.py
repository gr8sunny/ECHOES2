# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `VisionSystem.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()

# Start of module echoes
_M_echoes = Ice.openModule('echoes')
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('VisionSystem'):
    _M_echoes.VisionSystem = Ice.createTempClass()
    class VisionSystem(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.VisionSystem:
                raise RuntimeError('echoes.VisionSystem is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::VisionSystem')

        def ice_id(self, current=None):
            return '::echoes::VisionSystem'

        def ice_staticId():
            return '::echoes::VisionSystem'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def getAttentionProbability(self, x, y, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_VisionSystem)

        __repr__ = __str__

    _M_echoes.VisionSystemPrx = Ice.createTempClass()
    class VisionSystemPrx(Ice.ObjectPrx):

        def getAttentionProbability(self, x, y, _ctx=None):
            return _M_echoes.VisionSystem._op_getAttentionProbability.invoke(self, ((x, y), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.VisionSystemPrx.ice_checkedCast(proxy, '::echoes::VisionSystem', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.VisionSystemPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_VisionSystemPrx = IcePy.defineProxy('::echoes::VisionSystem', VisionSystemPrx)

    _M_echoes._t_VisionSystem = IcePy.defineClass('::echoes::VisionSystem', VisionSystem, (), True, None, (), ())
    VisionSystem.ice_type = _M_echoes._t_VisionSystem

    VisionSystem._op_getAttentionProbability = IcePy.Operation('getAttentionProbability', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_int), ((), IcePy._t_int)), (), IcePy._t_float, ())

    _M_echoes.VisionSystem = VisionSystem
    del VisionSystem

    _M_echoes.VisionSystemPrx = VisionSystemPrx
    del VisionSystemPrx

# End of module echoes
