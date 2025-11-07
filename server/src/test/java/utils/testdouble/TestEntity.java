package utils.testdouble;

public interface TestEntity<E, ID> {

	E asEntity();

	E asMockEntity(ID id);
}
