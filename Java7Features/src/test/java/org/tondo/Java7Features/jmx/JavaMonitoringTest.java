package org.tondo.Java7Features.jmx;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformManagedObject;
import java.lang.management.RuntimeMXBean;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.tondo.Java7Features.JavaFeaturesTestBase;

/**
 * JMX - Java management extensions.
 * Managed services are provided by Managed beans (MB vs MXB), which are registered
 * in server (agent)
 * 
 * @author TondoDev
 *
 */
public class JavaMonitoringTest extends JavaFeaturesTestBase{

	
	/**
	 * Using JMX to ivestigate state of current JVM
	 */
	@Test
	public void testJvmInfo() {
		// Management interface for JVM
		RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
		assertNotNull(mxBean);
		
		// other way how to get it
		RuntimeMXBean mxBean2 = ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
		assertEquals(mxBean, mxBean2);
		// there is only one instance of this at JVM
		assertSame(mxBean, mxBean2);
		
		// name of running JVM, is different for every running JVM
		assertTrue(mxBean.getName().endsWith("@AntonZsíros-PC")); // on my system
		
		// some values on my system
		assertEquals("Java Virtual Machine Specification", mxBean.getSpecName());
		assertEquals("Java HotSpot(TM) 64-Bit Server VM", mxBean.getVmName());
		assertNotNull(mxBean.getLibraryPath());
		assertEquals("Oracle Corporation", mxBean.getVmVendor());
	}
	
	/**
	 * Listing of supported MXBeans
	 */
	@Test
	public void testPlatformMXBeans() {
		// subinterfaces of PlatformManagedObject supperted in system
		Set<Class<? extends PlatformManagedObject>> interfaces = ManagementFactory.getPlatformManagementInterfaces();
		assertEquals(15, interfaces.size());

		// check if some known interfaces are in set
		assertTrue(interfaces.contains(RuntimeMXBean.class));
		assertTrue(interfaces.contains(OperatingSystemMXBean.class));
		
		Set<String> memmoryManagementBeanNames = new HashSet<>();
		for (MemoryManagerMXBean mmb : ManagementFactory.getPlatformMXBeans(MemoryManagerMXBean.class)) {
			memmoryManagementBeanNames.add(mmb.getName());
		}
		
		assertEquals(cSET("CodeCacheManager", "PS Scavenge", "PS MarkSweep"), memmoryManagementBeanNames);
	}
}

