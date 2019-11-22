package planning;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * An effectively static class to offer an interface to a different type of path planner
 * for different type of actors, where the type can be set and then (re)instantiated
 * while the program is running. Any time path planning is done in the program, the
 * appropriate planner from here should be used.
 */
public final class IPlanning {
	
	/**
	 * The type of planner to be used for generating paths for zombies. This class type
	 * uses a wildcard to ensure that it extends the abstract Planner class.
	 */
	private static Class<? extends Planner> ZOMBIES_PLANNER_TYPE = null;
	
	/**
	 * The planner to be used for generating paths for zombies.
	 */
	private static Planner ZOMBIES_PLANNER = null;
	
	/**
	 * The default constructor.
	 * This private scope, in tandem with the final modifier to the class, makes this
	 * effectively static / non-instantiable.
	 */
	private IPlanning() {}
	
	/**
	 * A helper function to find the expected constructor for a provided derived planner
	 * class, given the constructor's argument types and values.
	 * @param plannerType The planner type to instantiate.
	 * @param argTypes The constructor arguments' types.
	 * @param args The values to pass to the constructor.
	 * @return The new instance of some class derived from the planner class.
	 */
	private static Planner newPlanner(
			Class<? extends Planner> plannerType,
			Class<?>[] argTypes,
			Object... args) {
		Constructor<?>[] ctor = plannerType.getConstructors();
		
		for (Constructor<?> c : ctor) {
			Type[] t = c.getGenericParameterTypes();
			if (t.length == argTypes.length) {
				boolean found = true;
				
				for (int i = 0; i < t.length; i++) {
					if (!argTypes[i].getName().equals(t[i].getTypeName())) {
						found = false;
						break;
					}
				}
				
				if (found) {
					try {
						return (Planner)c.newInstance(args);
					} catch (InstantiationException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Setter for the type of planner to use for the zombies' path planning.
	 * @param plannerType The derived planner class.
	 */
	public static void setZombiesPlannerType(Class<? extends Planner> plannerType) {
		ZOMBIES_PLANNER_TYPE = plannerType;
	}
	
	/**
	 * Given the constructor's arguments, instantiate a new planner for the zombies.
	 * @param argTypes The constructor arguments' types.
	 * @param args The values to pass to the constructor.
	 * @return Whether or not the zombies planner was successfully created and set.
	 */
	public static boolean renewZombiesPlanner(Class<?>[] argTypes, Object... args) {
		Planner p = IPlanning.newPlanner(ZOMBIES_PLANNER_TYPE, argTypes, args);
		
		if (p != null) {
			ZOMBIES_PLANNER = p;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Getter for the path planner used for the zombie actors.
	 * @return The zombies' path planner.
	 */
	public static Planner getZombiesPlanner() {
		return ZOMBIES_PLANNER;
	}
}