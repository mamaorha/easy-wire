# easy-wire
SpringRunner alternative, only loading required beans. a lot easier and a lot faster to configure + run.
Easywire doesn't use SpringRunner, it is custom code that was written on the fly from given needs.

Annotate junit class with 
@RunWith(EasywireRunner.class)

you can also annotate with 
@EasywireProperties(basePackage = "co.il.nmh")

- make sure you use YOUR base package, this is used to scan the beans.
now you can just use @Inject of the classes you need and work as you were using SpringRunner.

Easywire currently supports:
-------------------
- injecting beans: @Inject, @Autowire
- special injection types: @Provider, @Optional, @Value, @Qualifier
- injection of mocks: @Mock, @Spy
- bean definitions: @Bean, @Primary, @Configuration, @Component, @Service, @Named, @Controller, @Repository, @Service

special use cases:
-------------------
1. overriding beans, in case you wish your custom bean to be loaded instead of the real implementation:
	- create a class that extend the original class / implement the interface - override what ever you wish with your own logic
	- create a class that implements 'IEasywireInitializer' - you should then provide the class in the EasywireProperties 'initializeClass', or class that implements IEasywireBaseInitializer
	- in the initialize method, call EasywireBeanFactory.INSTANCE.overrideBean(A.class, MockA.class) or if you wish you can push an instance instead of the class, for example EasywireBeanFactory.INSTANCE.pushBean(A.class, myInstance)

2. overriding logger, in some cases you wish to see that your method printed something to the logger so we will replace the logger with our own custom logger:
	- EasywireBeanFactory.INSTANCE.overrideLogger(A.class)
	- in the test you can now inject LoggerTest
	- use methods such as List<String> logs = loggerTest.getLogs(Level.INFO) or String lastLog = loggerTest.getLastLog(Level.INFO);

replace beans with mocks
-------------------------
#persistence

- real class
```xml
package co.il.nmh.example.persistence.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import co.il.nmh.example.persistence.dto.ExampleDTO;

@Repository
public interface IExampleRepository extends CrudRepository<ExampleDTO, String>
{
}
```

- mock
```xml
package co.il.nmh.example.tests.mock.persistence;

import co.il.nmh.example.persistence.dto.ExampleDTO;
import co.il.nmh.example.persistence.repositories.ICustomerRepository;

import co.il.nmh.easy.wire.utils.mock.persistence.CrudRepositoryMock;

/**
 * @author Maor Hamami
 *
 */

public class CustomerRepositoryMock extends CrudRepositoryMock<ExampleDTO, String> implements IExampleRepository
{
}
```
- context class
```xml
package co.il.nmh.example.tests.context;

import co.il.nmh.example.persistence.repositories.IExampleRepository;
import co.il.nmh.example.tests.mock.RestClientMock;
import co.il.nmh.example.tests.mock.persistence.ExampleRepositoryMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import co.il.nmh.easy.http.proxy.core.RestClient;
import co.il.nmh.easy.wire.core.EasywireBeanFactory;
import co.il.nmh.easy.wire.core.base.IEasywireInitializer;

public class ExampleManagementMockContext implements IEasywireInitializer
{
	@Override
	public void initialize()
	{
		EasywireBeanFactory.INSTANCE.overrideBean(IExampleRepository.class, ExampleRepositoryMock.class);
	}
}
```
