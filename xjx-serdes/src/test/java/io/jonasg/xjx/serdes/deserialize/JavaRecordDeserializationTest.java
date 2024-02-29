package io.jonasg.xjx.serdes.deserialize;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.XjxSerdes;

import java.util.List;

public class JavaRecordDeserializationTest {

	@Test
	void deserializeTopLevelRecord() {
		// given
		record Person(@Tag(path = "/Person/name") String name) {}
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<Person>
					<name>John</name>
				</Person>
				""";

		// when
		Person person = new XjxSerdes().read(data, Person.class);

		// then
		assertThat(person.name()).isEqualTo("John");
	}

	@Test
	void recordAsField() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<House>
				  <Person>
				    <name>John</name>
				  </Person>
				</House>
				""";

		// when
		House house = new XjxSerdes().read(data, House.class);

		// then
		assertThat(house.person.name()).isEqualTo("John");
	}

	@Test
	void relativeMappedFieldWithWithTopLevelMappedRootType() {
		// given
		@Tag(path = "/House")
		record Person(@Tag(path = "Person/name") String name) {}
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<House>
				  <Person>
				    <name>John</name>
				  </Person>
				</House>
				""";

		// when
		Person person = new XjxSerdes().read(data, Person.class);

		// then
		assertThat(person.name()).isEqualTo("John");
	}


	@Test
	void absoluteMappedFieldWithWithTopLevelMappedRootType() {
		// given
		@Tag(path = "/House")
		record Person(@Tag(path = "/House/Person/name") String name) {}
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<House>
				  <Person>
				    <name>John</name>
				  </Person>
				</House>
				""";

		// when
		Person person = new XjxSerdes().read(data, Person.class);

		// then
		assertThat(person.name()).isEqualTo("John");
	}

	@Test
	void recordWithComplexType() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<Computer>
				  <Brand>
				    <Name>Apple</Name>
				  </Brand>
				</Computer>
				""";

		// when
		Computer computer = new XjxSerdes().read(data, Computer.class);

		// then
		assertThat(computer.brand.name).isEqualTo("Apple");
	}

	@Test
	void recordWithListType() {
		// given
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<Computers>
				  <Brand>
				    <Name>Apple</Name>
				  </Brand>
				  <Brand>
				    <Name>Commodore</Name>
				  </Brand>
				</Computers>
				""";

		// when
		Computers computers = new XjxSerdes().read(data, Computers.class);

		// then
		assertThat(computers.brand).hasSize(2);
	}

	@Test
	void setFieldsToNullWhenNoMappingIsFound() {
		// given
		record Person(@Tag(path = "/Person/name") String name, String lastName) {}
		String data = """
				<?xml version="1.0" encoding="UTF-8"?>
				<Person>
					<name>John</name>
				</Person>
				""";

		// when
		Person person = new XjxSerdes().read(data, Person.class);

		// then
		assertThat(person.lastName()).isNull();
	}

	record Person(@Tag(path = "name") String name, String lastName) {}

	static class House {
		@Tag(path = "/House/Person")
		Person person;

		public House() {
		}
	}

	record Computer(@Tag(path = "/Computer/Brand") Brand brand) {}

	static class Brand {
		@Tag(path = "Name")
		String name;

		public Brand() {
		}
	}

	record Computers(@Tag(path = "/Computers", items = "Brand") List<Brand> brand) {}

}
