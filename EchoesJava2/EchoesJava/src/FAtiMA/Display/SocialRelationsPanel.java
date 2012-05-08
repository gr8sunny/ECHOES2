package FAtiMA.Display;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import FAtiMA.Agent;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.socialRelations.LikeRelation;
import FAtiMA.socialRelations.Relation;
import FAtiMA.socialRelations.RespectRelation;

public class SocialRelationsPanel extends AgentDisplayPanel {

	private static final long serialVersionUID = 1L;

	JPanel _relationsPanel;

	protected Hashtable _realationsDisplay;

	public SocialRelationsPanel() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		_realationsDisplay = new Hashtable();

		_relationsPanel = new JPanel();
		_relationsPanel.setLayout(new BoxLayout(_relationsPanel,
				BoxLayout.Y_AXIS));

		JScrollPane relationsScroll = new JScrollPane(_relationsPanel);
		relationsScroll.setBorder(BorderFactory
				.createTitledBorder("Social Relations"));

		this.add(relationsScroll);
	}

	public boolean Update(Agent ag) {
		ArrayList relations = LikeRelation.getAllRelations(AutobiographicalMemory
				.GetInstance().getSelf());
		relations.addAll(RespectRelation.getAllRelations(AutobiographicalMemory
				.GetInstance().getSelf()));
		boolean updated = false;

		// in this case, there's a new relation added (it is not usual for
		// relations to disapear)
		// so we have to clear all relations and start displaying them all again
		if (_realationsDisplay.size() != relations.size()) {
			_relationsPanel.removeAll(); // removes all displayed emotions
											// from the panel
			_realationsDisplay.clear();
			Iterator it = relations.iterator();
			while (it.hasNext()) {
				Relation r = (Relation) it.next();
				RelationDisplay display = new RelationDisplay(r);
				_relationsPanel.add(display.getPanel());
				_realationsDisplay.put(r.getHashKey(), display);
			}
			updated = true;
		}

		Iterator it = relations.iterator();
		while (it.hasNext()) {
			Relation r = (Relation) it.next();
			RelationDisplay display = (RelationDisplay) _realationsDisplay
					.get(r.getHashKey());
			display.setValue(r.getValue());
		}

		return updated;
	}
}
