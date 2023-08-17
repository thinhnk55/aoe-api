package vn.vietdefi.aoe.services;

import vn.vietdefi.aoe.services.caster.CasterService;
import vn.vietdefi.aoe.services.caster.ICasterService;
import vn.vietdefi.aoe.services.gamer.GamerService;
import vn.vietdefi.aoe.services.gamer.IGamerService;
import vn.vietdefi.aoe.services.match.IMatchService;
import vn.vietdefi.aoe.services.match.MatchService;
import vn.vietdefi.aoe.services.star.IStarService;
import vn.vietdefi.aoe.services.star.StarService;

public class AoeServices {
    public static IGamerService gamerService = new GamerService();
    public static ICasterService casterService = new CasterService();

    public static IMatchService matchService = new MatchService();
    public static IStarService starService = new StarService();

}
