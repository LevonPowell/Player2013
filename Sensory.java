package MasterPlayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;

/**
 * Attempt at separating functionality.
 * 
 * This contains all the global sensory functions for soldiers.
 * 
 * @author levonpowell
 */
public class Sensory 
{
//	private static final int DEFAULT_ENEMY_DETECTION_RANGE = 15;
	
	/**
	 * Returns the number of allied robots in the vicinity.
	 * 
	 * @param rc the RobotController
	 * @param sightRange the robot's sight range
	 * @return the number of enemies within a certain radius
	 */
	public static int getNumberOfLocalEnemies( RobotController rc, int sightRange )
	{
		return localTeamRobotCount( rc, sightRange, true );
	}
	
	/**
	 * Returns the number of allied robots in the vicinity.
	 * 
	 * @param rc the RobotController
	 * @param sightRange the robot's sight range
	 * @return the number of allies within a certain radius
	 */
	public static int getNumberOfLocalAllies( RobotController rc, int sightRange )
	{
		return localTeamRobotCount( rc, sightRange, false );
	}
	
	/**
	 * Returns the direction (relative to the robot) towards the enemy HQ
	 * 
	 * @param rc
	 * @return 
	 */
	public static Direction getDirectionToEnemyHQ( RobotController rc )
	{
		return directionsToHQ( rc, true );
	}
	
	public static Direction getDirectionToHQ( RobotController rc )
	{
		return directionsToHQ( rc, false );
	}
	
	// End public methods.
	// Being private methods
	
	/**
	 * Returns the location of either the allied, or enemy HQ
	 * 
	 * @param rc
	 * @param enemyTeam
	 * @return 
	 */
	private static Direction directionsToHQ( RobotController rc, boolean enemyTeam )
	{
		MapLocation hqSquare;
		
		if ( enemyTeam )
		{
			hqSquare = rc.senseEnemyHQLocation();
		}
		else
		{
			hqSquare = rc.senseHQLocation();
		}
		
		return rc.getLocation().directionTo( hqSquare );
	}
	
	/**
	 * This does all the lifting when asking "how many allies/enemies are around me?"
	 * 
	 * Implements the NumberOfLocalEnemies/Allies methods.
	 * 
	 * @param rc
	 * @param range an integer of the sight range of the robot
	 * @param enemies a boolean asking whether you want to count enemies or allies.
	 * @return
	 */
	private static int localTeamRobotCount( RobotController rc, int range, boolean enemies )
	{
		Robot[] robotArray;
		
		Team team = rc.getTeam();
		
		if ( enemies )
		{
			team = team.opponent();
		}
		
		robotArray = rc.senseNearbyGameObjects( Robot.class, range, team );
		
		return robotArray.length;		
	}
}
