package co.il.nmh.easy.wire.core.base;

/**
 * @author Maor Hamami
 * 
 *         Classes that will implement this interface will be loaded by Easywire before the flow begins. use case example: 1. developer A created a project that uses Cassandra, he wish to override the cassandra connector with mocks for the Easywire, he will create a class that implement the IEasywireBaseInitializer and prepare the logic there 2. develope B will have dependency in the other project (that have Cassandra), he will automatically get the mocks that developer A built and the beans
 *         will be overriden
 */

public interface IEasywireBaseInitializer extends IEasywireInitializer
{

}
