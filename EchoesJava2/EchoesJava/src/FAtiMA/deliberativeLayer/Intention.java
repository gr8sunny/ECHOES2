/** 
 * Intention.java - Represents an explicit intention to achieve a goal
 *  
 * Copyright (C) 2006 GAIPS/INESC-ID 
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Company: GAIPS/INESC-ID
 * Project: FAtiMA
 * Created: 14/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 14/01/2004 - File created
 * João Dias: 24/05/2006 - Added comments to each public method's header
 * João Dias: 24/05/2006 - Removed the Intention's type from the class (was not being used)
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 17/07/2007 - Instead of storing two instances of emotions (Hope and Fear),
 * 						   the class now stores only the hashkeys of such emotions in order
 * 						   to make the class easily serializable
 */
package FAtiMA.deliberativeLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import FAtiMA.deliberativeLayer.goals.ActivePursuitGoal;
import FAtiMA.deliberativeLayer.goals.Goal;
import FAtiMA.deliberativeLayer.plan.Plan;
import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.emotionalState.EmotionalState;

/**
 * Represents an explicit intention to achieve an ActivePursuitGoal
 * 
 * @author João Dias
 */
public class Intention implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int MAXPLANS = 100;

	private String _fearEmotionID;
	private ActivePursuitGoal _goal;
	private String _hopeEmotionID;
	private ArrayList _planConstruction;
	private Plan _emptyPlan;

	private boolean _anActionWasMade;

	/**
	 * Creates a new Intention to a achieve a goal
	 * 
	 * @param p
	 *            - the initial empty plan to achieve the goal
	 * @param g
	 *            - the goal that the intention tries to achieve
	 * @see Plan
	 * @see Goal
	 */
	public Intention(Plan p, ActivePursuitGoal g) {
		_emptyPlan = (Plan) p.clone();
		_planConstruction = new ArrayList();
		_planConstruction.add(p);
		_goal = g;
		_anActionWasMade = false;
		_fearEmotionID = null;
		_hopeEmotionID = null;
	}

	/**
	 * Adds a plan to the set of alternative plans that the agent has to achieve
	 * the intention
	 * 
	 * @param p
	 *            - the Plan to add
	 * @see Plan
	 */
	public void AddPlan(Plan p) {
		if (_planConstruction.size() <= MAXPLANS) {
			_planConstruction.add(p);
		}
	}

	/**
	 * Gets the Fear emotion associated with the intention. This fear is caused
	 * by the prospect of failling to achieve the goal
	 * 
	 * @return - the Fear emotion
	 */
	public ActiveEmotion GetFear() {
		if (_fearEmotionID == null)
			return null;
		return EmotionalState.GetInstance().GetEmotion(_fearEmotionID);
	}

	/**
	 * indicates if any action was executed in order to achieve the intention or
	 * not
	 * 
	 * @return true if any action was executed for the intention, false
	 *         otherwise
	 */
	public boolean AnActionWasMade() {
		return _anActionWasMade;
	}

	/**
	 * Sets that an action was executed for the intention
	 * 
	 * @param b
	 *            - if b is true it means that a action was executed for this
	 *            intention
	 */
	public void SetAnActionWasMade(boolean b) {
		_anActionWasMade = b;
	}

	/**
	 * Gets the goal that intention corresponds to
	 * 
	 * @return the intention's goal
	 */
	public ActivePursuitGoal getGoal() {
		return _goal;
	}

	/**
	 * Gets the Home emotion associated with the intention. This hope is caused
	 * by the prospect of succeeding in achieving the goal
	 * 
	 * @return - the Hope emotion
	 */
	public ActiveEmotion GetHope() {
		if (_hopeEmotionID == null)
			return null;
		return EmotionalState.GetInstance().GetEmotion(_hopeEmotionID);
	}

	/**
	 * Gets the best plan developed so far to achieve the intention
	 * 
	 * @return the best plan
	 */
	public Plan GetBestPlan() {
		ListIterator li;
		Plan p = _emptyPlan;
		Plan bestPlan = null;
		float minH = 9999999;

		/**
		 * instead of getting the best plan according to method h() - which is
		 * irrelevant in our case, it chooses randomly. This ensures that the
		 * agent doesn't always choose the same strategy (e.g. not always blink)
		 * Perhaps will need to update this to choosing randomly from the plans
		 * with the highest probability.
		 */
		// existing FATIMA code

		li = _planConstruction.listIterator();
		while (li.hasNext()) {
			p = (Plan) li.next();
			// if (p.h() < minH) {
			// bestPlan = p;
			// minH = p.h();
			// }
			if (p.getChosenRecently()) {
				bestPlan = p;
			}
		}
		if (p.getChosenRecently() == false) {
			p.setChosenRecently(true);

			bestPlan = p;
		}

		return bestPlan;
	}

	/**
	 * Gets the likelihood of the agent achieving the intention
	 * 
	 * @return a float value representing the probability [0;1]
	 */
	public float GetProbability() {
		ListIterator li;
		float p;
		float bestProb = 0;
		li = _planConstruction.listIterator();
		while (li.hasNext()) {
			p = ((Plan) li.next()).getProbability();
			// System.out.println("current prob: " + p);

			if (p > bestProb)
				bestProb = p;
		}
		return bestProb;
	}

	/**
	 * Gets the number of alternative plans that the agent has to achieve the
	 * intention
	 * 
	 * @return
	 */
	public int NumberOfAlternativePlans() {
		return _planConstruction.size();
	}

	/**
	 * Removes the last plan from the list of alternative plans
	 * 
	 * @return the removed Plan
	 */
	public Plan RemovePlan() {
		return (Plan) _planConstruction.remove(_planConstruction.size() - 1);
	}

	/**
	 * Removes the received plan from the list of alternative plans
	 * 
	 * @param p
	 *            - the plan to remove
	 */
	public void RemovePlan(Plan p) {
		_planConstruction.remove(p);
	}

	/**
	 * Sets the Fear emotion associated with the intention. This fear is caused
	 * by the prospect of failling to achieve the goal
	 * 
	 * @param fear
	 *            - the Fear emotion to associate with the intention
	 */
	public void SetFear(ActiveEmotion fear) {
		if (fear != null)
			_fearEmotionID = fear.GetHashKey();
	}

	/**
	 * Sets the Hope emotion associated with the intention. This hope is caused
	 * by the prospect of succeeding in achieving the goal
	 * 
	 * @param hope
	 *            - the hope emotion to associate with the intention
	 */
	public void SetHope(ActiveEmotion hope) {
		if (hope != null)
			_hopeEmotionID = hope.GetHashKey();
	}

	/**
	 * Converts the intention to a String
	 * 
	 * @return the converted String
	 */
	public String toString() {
		return "Intention: " + _goal;
	}

	/**
	 * Updates all the plans for the intention according to the new state of the
	 * world. Supports continuous planning.
	 */
	public void CheckLinks() {
		ListIterator li;
		li = _planConstruction.listIterator();

		while (li.hasNext()) {
			((Plan) li.next()).UpdatePlan();
		}

	}

	/**
	 * Updates the probability of achieving the intention This function should
	 * be called whenever the plans change
	 */
	public void UpdateProbabilities() {
		ListIterator li;
		li = _planConstruction.listIterator();

		while (li.hasNext()) {
			((Plan) li.next()).UpdateProbabilities();
		}
	}

	public ArrayList getPlans() {
		return _planConstruction;
	}

}