package MasterPlayer;

import battlecode.common.Clock;

/**
 *
 * @author levonpowell
 */
public class Behaviour
{
	/**
	 * This returns true if the game is getting late.
	 * 
	 * @return 
	 */
	public static boolean getIsTimeRunningOut()
	{
		 return Clock.getRoundNum() > 1000;
	}
}
