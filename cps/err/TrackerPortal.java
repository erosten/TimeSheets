
package cps.err;

import cps.core.db.frame.DerbyDatabase;
import cps.core.db.frame.DerbyDatabaseDAO;
import cps.err.model.Bug;
import cps.err.model.SolutionStep;

import java.sql.SQLException;
import java.util.List;

public class TrackerPortal extends DerbyDatabaseDAO {

  public TrackerPortal(DerbyDatabase DerbyDB) throws SQLException {
    super(DerbyDB);
  }

  public boolean addBug(Bug bug) {
    return super.add(Bug.class, bug);
  }

  public boolean removeBug(String id) {
    Bug bug = super.findById(Bug.class, id);
    bug.removeSolutionSteps(bug.getSolutionSteps());
    super.save();
    return super.remove(bug);
  }

  public boolean addSolutionStep(String bugId, String solutionStep) {
    Bug bug = super.findById(Bug.class, bugId);
    int SSNum = bug.getNextSolutionStepNum();
    bug.addSolutionStep(solutionStep);
    return super.add(SolutionStep.class, bug.getSolutionStep(SSNum));
  }

  public int getNextBugNum() {
    final int totalBugs = 1;
    final int bugsFound = findAllBugs().size();
    return bugsFound + totalBugs;
  }

  /**
   * Returns all the bugs
   * 
   * @return a tree set of all valid bugs
   */
  public List<Bug> findAllBugs() {
    return super.findAll(Bug.class);
  }

  public Bug findById(String id) {
    return super.findById(Bug.class, id);
  }

}
