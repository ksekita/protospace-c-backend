package in.techcamp.protospace.repository;

import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.mapper.PrototypeMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PrototypeRepository {

  private final PrototypeMapper prototypeMapper;

  public PrototypeRepository(PrototypeMapper prototypeMapper) {
    this.prototypeMapper = prototypeMapper;
  }

  public PrototypeEntity findById(Long id) {
    return prototypeMapper.findById(id);
  }
}