package org.intalio.tempo.workflow.wds.core.tms;

import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.ItemDaoConnection;
import org.intalio.tempo.workflow.wds.core.JPAItemDaoConnectionFactory;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class JPAWDSTest {

	final static ExpectThat expect = new ExpectThatImpl();

	Item item;
	@Subject
	JPAItemDaoConnectionFactory factory;
	@Subject
	ItemDaoConnection jpac;

	@BeforeSpecification
	void before() {
		item = WDSUtil.getSampleItem();
		factory = new JPAItemDaoConnectionFactory();
		jpac = factory.getItemDaoConnection();
	}

	@Specification
	public void canStoreItems() throws Exception {
		jpac.storeItem(item);
		expect.that(item.equals(jpac.retrieveItem(item.getURI())));
		jpac.deleteItem(item.getURI());
		try {
			jpac.retrieveItem(item.getURI());
			expect.that(false);
		} catch (Exception e) {
			expect.that(true);
		}
	}
}