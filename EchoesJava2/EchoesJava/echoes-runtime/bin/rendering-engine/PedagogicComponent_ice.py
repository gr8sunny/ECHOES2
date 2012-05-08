# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

# Ice version 3.3.1
# Generated from file `PedagogicComponent.ice'

import Ice, IcePy, __builtin__

if not Ice.__dict__.has_key("_struct_marker"):
    Ice._struct_marker = object()
import Common_ice

# Included module echoes
_M_echoes = Ice.openModule('echoes')

# Start of module echoes
__name__ = 'echoes'

if not _M_echoes.__dict__.has_key('PedagogicComponent'):
    _M_echoes.PedagogicComponent = Ice.createTempClass()
    class PedagogicComponent(Ice.Object):
        def __init__(self):
            if __builtin__.type(self) == _M_echoes.PedagogicComponent:
                raise RuntimeError('echoes.PedagogicComponent is an abstract class')

        def ice_ids(self, current=None):
            return ('::Ice::Object', '::echoes::PedagogicComponent')

        def ice_id(self, current=None):
            return '::echoes::PedagogicComponent'

        def ice_staticId():
            return '::echoes::PedagogicComponent'
        ice_staticId = staticmethod(ice_staticId)

        #
        # Operation signatures.
        #
        # def loadChildProfile(self, name, current=None):
        # def changeScene(self, current=None):
        # def sendActionStartedAEnameAndArgs(self, actionName, actionArgs, current=None):

        def __str__(self):
            return IcePy.stringify(self, _M_echoes._t_PedagogicComponent)

        __repr__ = __str__

    _M_echoes.PedagogicComponentPrx = Ice.createTempClass()
    class PedagogicComponentPrx(Ice.ObjectPrx):

        def loadChildProfile(self, name, _ctx=None):
            return _M_echoes.PedagogicComponent._op_loadChildProfile.invoke(self, ((name, ), _ctx))

        def changeScene(self, _ctx=None):
            return _M_echoes.PedagogicComponent._op_changeScene.invoke(self, ((), _ctx))

        def sendActionStartedAEnameAndArgs(self, actionName, actionArgs, _ctx=None):
            return _M_echoes.PedagogicComponent._op_sendActionStartedAEnameAndArgs.invoke(self, ((actionName, actionArgs), _ctx))

        def checkedCast(proxy, facetOrCtx=None, _ctx=None):
            return _M_echoes.PedagogicComponentPrx.ice_checkedCast(proxy, '::echoes::PedagogicComponent', facetOrCtx, _ctx)
        checkedCast = staticmethod(checkedCast)

        def uncheckedCast(proxy, facet=None):
            return _M_echoes.PedagogicComponentPrx.ice_uncheckedCast(proxy, facet)
        uncheckedCast = staticmethod(uncheckedCast)

    _M_echoes._t_PedagogicComponentPrx = IcePy.defineProxy('::echoes::PedagogicComponent', PedagogicComponentPrx)

    _M_echoes._t_PedagogicComponent = IcePy.defineClass('::echoes::PedagogicComponent', PedagogicComponent, (), True, None, (), ())
    PedagogicComponent.ice_type = _M_echoes._t_PedagogicComponent

    PedagogicComponent._op_loadChildProfile = IcePy.Operation('loadChildProfile', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string),), (), None, ())
    PedagogicComponent._op_changeScene = IcePy.Operation('changeScene', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (), (), None, ())
    PedagogicComponent._op_sendActionStartedAEnameAndArgs = IcePy.Operation('sendActionStartedAEnameAndArgs', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, (), (((), IcePy._t_string), ((), _M_echoes._t_StringSeq)), (), None, ())

    _M_echoes.PedagogicComponent = PedagogicComponent
    del PedagogicComponent

    _M_echoes.PedagogicComponentPrx = PedagogicComponentPrx
    del PedagogicComponentPrx

# End of module echoes
