package vn.vietdefi.aoe.services;

import vn.vietdefi.aoe.services.caster.CasterService;
import vn.vietdefi.aoe.services.caster.ICasterService;
import vn.vietdefi.aoe.services.gamer.GamerService;
import vn.vietdefi.aoe.services.gamer.IGamerService;

public class AoeServices {
    public static IGamerService gamerService = new GamerService();
    public static ICasterService casterService = new CasterService();

}
