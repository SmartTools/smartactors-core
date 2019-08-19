package info.smart_tools.smartactors.das.models;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public class UploadRepository {

    private String id;
    private String url;

    public UploadRepository(final String id, final String url)
            throws Exception {
        if (null == id || null == url) {
            throw new Exception("UploadRepository:Constructor - Repository arguments could not be null.");
        }
        this.id = id;
        this.url = url;
    }

    public UploadRepository(final IObject repository)
            throws Exception {
        try {
            this.id = (String) repository.getValue(new FieldName("uploadRepositoryId"));
            this.url = (String) repository.getValue(new FieldName("uploadRepositoryUrl"));
        } catch (InvalidArgumentException | ReadValueException e) {
            throw new Exception("UploadRepository:Constructor - failed.", e);
        }
    }

    public IObject asIObject()
            throws Exception {
        try {
            IObject repository = new DSObject();
            repository.setValue(new FieldName("uploadRepositoryId"), this.id);
            repository.setValue(new FieldName("uploadRepositoryUrl"), this.url);

            return repository;
        } catch (ChangeValueException e) {
            throw new Exception("Actor:asIObject - failed.", e);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
