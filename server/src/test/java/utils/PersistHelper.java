package utils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PersistHelper {

	private final EntityManager em;

	private PersistHelper(EntityManager em) {
		this.em = em;
	}

	public static PersistHelper start(EntityManager em) {
		return new PersistHelper(em);
	}

	public <E> PersistHelper persist(Collection<E> entities) {
		for (E entity : entities) {
			this.em.persist(entity);
		}
		return this;
	}

	public <E> PersistHelper persist(E... entities) {
		for (E entity : entities) {
			this.em.persist(entity);
		}
		return this;
	}

	public PersistHelper and() {
		return this;
	}

	public PersistHelper flush() {
		this.em.flush();
		return this;
	}

	public PersistHelper clear() {
		this.em.clear();
		return this;
	}


	public void flushAndClear() {
		this.flush().clear();
	}

	public <E> List<E> persistAndReturn(Collection<E> entities) {
		for (E entity : entities) {
			this.em.persist(entity);
		}
		return new ArrayList<>(entities);
	}

	@SafeVarargs
	public final <E> List<E> persistAndReturn(E... entities) {
		for (E entity : entities) {
			this.em.persist(entity);
		}
		return Arrays.stream(entities).collect(Collectors.toList());
	}

	public <E> E persistAndReturn(E entity) {
		this.em.persist(entity);
		this.flushAndClear();
		return entity;
	}
}

