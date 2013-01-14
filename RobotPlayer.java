package MasterPlayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * This is my "master" player, as in the Master branch of a repository.
 * 
 * As of Monday January 14th, 2013, this is simply "aggressive player" with
 * some of it's behaviour factored and abstracted in the Sensory class.
 * 
 * HQ Behaviours:
 * 
 * If the closest square to the enemy cannot spawn, pick another one randomly.
 * 
 * Senses nuke research now.
 * 
 * Solider Behaviours:
 * 
 * @author levonpowell
 */
public class RobotPlayer 
{
	private static final double MINE_LAYING_PROBABILITY = 0.01;
	
	private static int  ENEMY_DETECTION_RANGE = 15;
	private static int  TEAM_DETECTION_RANGE = 7;
	
	/**
	 * This is the only required method of this class. I will likely want to break a bunch of
	 * this functionality into new classes.
	 * 
	 * @param rc is the base class which provides the majority of the functionality.
	 */
	public static void run( RobotController rc ) 
	{
		while ( true ) 
		{
			try 
			{
				if ( rc.getType() == RobotType.HQ ) 
				{
					HQActions( rc ); // This handles all HQ actions.
				} 
				else if ( rc.getType() == RobotType.SOLDIER )
				{
					soldierActions( rc );
				}
				// End turn
				rc.yield(); // yield returns all extra bytecodes, and makes sure nothing is left over for next round.
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private static void HQActions( RobotController robotController ) throws GameActionException
	{
		if ( robotController.isActive() ) 
		{
			// This is the earliest round they can be half way done.
			
			// This also needs to not sweat it if it's a small map.
			if ( Clock.getRoundNum() > 199 )
			{
				boolean enemyNukeResearch = robotController.senseEnemyNukeHalfDone();
				
				// Then do something I guess.
			}
			
			// Spawn a soldier in the direction of the enemy camp
			Direction dir = Sensory.getDirectionToEnemyHQ( robotController );

			if ( robotController.canMove( dir ) )
			{
				robotController.spawn( dir );
			}
			else
			{
				Direction random = Direction.values()[ ( int )( Math.random()*8 ) ];
				
				if ( robotController.canMove( random ) )
				{
					robotController.spawn( random );
				}
			}
		}
	}
	
	private static void soldierActions( RobotController robotController ) throws GameActionException
	{
		if ( robotController.isActive() ) 
		{
			Direction dir; // Generic direction initialization
			int allies, enemies;
			
			// Get a map of all the enemies. If none are nearby, lay some mines down.
			
			enemies = Sensory.getNumberOfLocalEnemies( robotController, ENEMY_DETECTION_RANGE );
			allies = Sensory.getNumberOfLocalAllies( robotController, TEAM_DETECTION_RANGE );
			
			robotController.setIndicatorString( 1, "Enemy Soldiers in proximity: " + enemies );
			robotController.setIndicatorString( 2, "Team Soldiers in proximity: " + allies );
			
			if ( Math.random() < MINE_LAYING_PROBABILITY && enemies < 1 && !Behaviour.getIsTimeRunningOut() ) 
			{
				// Lay a mine 
				if( robotController.senseMine( robotController.getLocation() ) == null )
				{
					robotController.layMine();
				}
			} 
			else
			{
				// Half of the time I want new units moving towards the enemy base anyways.
				if ( enemies - allies > 2 )
				{
					// Fall back until there is a numbers advantage
					dir = Sensory.getDirectionToHQ( robotController );
				}
				// If the game is early, see if we move randomly.
				else if ( Math.random() < 0.45 && !Behaviour.getIsTimeRunningOut() )
				{
					dir = Direction.values()[ ( int )( Math.random()*8 ) ];
				}
				else
				{
					// If late game, move towards the enemy base.
					if ( Behaviour.getIsTimeRunningOut() )
					{
						if ( Math.random() < 0.025 )
						{
							dir = Direction.values()[ ( int )( Math.random()*8 ) ];
						}
						else
						{
							dir = Sensory.getDirectionToEnemyHQ( robotController );
						}
					}
					else
					{
						// Default move towards enemy base code.
						dir = Sensory.getDirectionToEnemyHQ( robotController );
					}
				}
				// Final movement.
				if( robotController.canMove( dir ) ) 
				{
					MapLocation moveTo = robotController.getLocation().add( dir );

					if ( robotController.senseMine( moveTo ) == null || robotController.senseMine( moveTo ) == robotController.getTeam() )
					{
						robotController.move( dir );

						robotController.setIndicatorString( 0, "Last direction moved: " + dir.toString() );									
					}
					else // Defusing code.
					{
						if ( enemies >= 1 || allies >= 6 )
						{
							robotController.defuseMine( moveTo );
						}
						else
						{
							robotController.yield(); // If there are too many enemies to defuse, do nothing.
						}
					}
				}
			}
			if ( Math.random() < 0.01 && robotController.getTeamPower() > 5 ) 
			{
				// Write the number 5 to a position on the message board corresponding to the robot's ID
				robotController.broadcast( robotController.getRobot().getID()%GameConstants.BROADCAST_MAX_CHANNELS, 5 );
			}			
		}		
	}
}


