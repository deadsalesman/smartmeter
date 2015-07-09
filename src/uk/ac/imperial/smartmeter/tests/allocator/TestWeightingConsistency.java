package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.allocator.RescherArbiter;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.impl.HighLevelController;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.User;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestWeightingConsistency extends GenericTest {

	TestWeightingConsistency() {
		super();
	}

	@Override
	public boolean doTest() {
		UserAgent u = new UserAgent(TicketTestHelper.uma, 8., 1., 6., 5.);
		UserAgent j = new UserAgent(TicketTestHelper.john, 1., 1., 3., 4.);
		UserAgent s = new UserAgent(TicketTestHelper.sam, 8., 1., 9., 7.);

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

		Double totalEnergy = 0.;
		Double totalWeight = 0.;

		for (Entry<UserAgent, Double> e : x.entrySet()) {
			for (ElectricityRequirement r1 : e.getKey().getReqs()) {
				totalEnergy += r1.getMaxConsumption() * r1.getDuration();
			}
			totalWeight += e.getValue();
		}

		return (totalEnergy.equals(totalWeight));
	}

}
