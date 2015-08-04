package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.allocator.RescherArbiter;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestWeightingConsistency extends GenericTest {

	TestWeightingConsistency() {
		super();
	}

	@Override
	public boolean doTest() {
		UserAgent u = new UserAgent(TicketTestHelper.user1, 8., 1., 6., 5.);
		UserAgent j = new UserAgent(TicketTestHelper.user2, 1., 1., 3., 4.);
		UserAgent s = new UserAgent(TicketTestHelper.user3, 8., 1., 9., 7.);

		TicketTestHelper.bindRequirement(u, 1.1, 2.3, 4, 3.);
		TicketTestHelper.bindRequirement(j, 3.1, 4.3, 7, 3);
		TicketTestHelper.bindRequirement(j, 5.1, 6.3, 3, 3);
		TicketTestHelper.bindRequirement(s, 7.1, 8.3, 4, 3);

		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(u);
		m.add(j);
		m.add(s);

		RescherArbiter r = new RescherArbiter();
		Map<UserAgent, Double> x = r.getWeighting(m);

		Double totalWeight = 0.;

		for (Entry<UserAgent, Double> e : x.entrySet()) {
			totalWeight += e.getValue();
		}
		Double desiredTotal = 2.5*(m.getSize())*(m.getSize()+1);
		return (desiredTotal.equals(totalWeight));
	}

}
