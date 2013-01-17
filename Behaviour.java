package MasterPlayer;

import battlecode.common.Clock;
import battlecode.common.RobotController;

/**
 *
 * @author levonpowell
 */
public class Behaviour
{
	private static final int ENEMY_DETECTION_RANGE = 15;
	private static final int TEAM_DETECTION_RANGE = ENEMY_DETECTION_RANGE - 5;	
	
	/**
	 * This returns true if the game is getting late.
	 * 
	 * @return 
	 */
	public static boolean getIsTimeRunningOut()
	{
		 return Clock.getRoundNum() > 1000;
	}
	
	/**
	 * What are the ideal conditions for planting a mine?
	 * 
	 * 
	 * @return 
	 */
	public static boolean shouldIPlantAMine( RobotController robotController, int enemy_range )
	{
		int enemies = Sensory.getNumberOfLocalEnemies( robotController, enemy_range );
		
		// This should only plant a mine if near a strategic point.
		if ( enemies == 0 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * As of now, if we are outnumbered, retreat.
	 * 
	 * @param robotController
	 * @return 
	 */
	public static boolean shouldIRetreat( RobotController robotController, int enemy_range, int team_range )
	{
		int enemies = Sensory.getNumberOfLocalEnemies( robotController, enemy_range );
		int allies = Sensory.getNumberOfLocalAllies( robotController, team_range );
		
		return ( enemies > allies );
	}
	
	/**
	 * My robots will be aggressive in the early game, play defensively mid game and
	 * be aggressive again in the late game.
	 * 
	 * @param robotController
	 * @return 
	 */
	public static boolean shouldIBeAggressive( RobotController robotController )
	{	
		if ( !Behaviour.getIsTimeRunningOut() )
		{
			return true; // Be very aggressive if the game is almost over.
		}
		else
		{
			if ( Clock.getRoundNum() < 500 ) // Be aggressive early on
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
