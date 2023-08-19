package vn.vietdefi.aoe.services;

import vn.vietdefi.aoe.services.clan.ClanService;
import vn.vietdefi.aoe.services.clan.IClanService;
import vn.vietdefi.aoe.services.event.EventService;
import vn.vietdefi.aoe.services.event.IEventService;
import vn.vietdefi.aoe.services.profile.IProfileService;
import vn.vietdefi.aoe.services.profile.ProfileService;
import vn.vietdefi.aoe.services.user.IUserService;
import vn.vietdefi.aoe.services.user.UserService;
import vn.vietdefi.aoe.services.donate.DonateService;
import vn.vietdefi.aoe.services.donate.IDonateService;
import vn.vietdefi.aoe.services.user.caster.CasterService;
import vn.vietdefi.aoe.services.user.caster.ICasterService;
import vn.vietdefi.aoe.services.user.gamer.GamerService;
import vn.vietdefi.aoe.services.user.gamer.IGamerService;
import vn.vietdefi.aoe.services.match.IMatchService;
import vn.vietdefi.aoe.services.match.MatchService;
import vn.vietdefi.aoe.services.star.IStarService;
import vn.vietdefi.aoe.services.star.StarService;

public class AoeServices {
    public static IGamerService gamerService = new GamerService();
    public static ICasterService casterService = new CasterService();
    public static IClanService clanService = new ClanService();
    public static IMatchService matchService = new MatchService();
    public static IStarService starService = new StarService();
    public static IUserService userService = new UserService();
    public static IProfileService profileService = new ProfileService();
    public static IDonateService donateService = new DonateService();
    public static IEventService eventService = new EventService();

}
