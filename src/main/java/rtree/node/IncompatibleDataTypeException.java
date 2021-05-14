package rtree.node;

public class IncompatibleDataTypeException extends Exception {
    public IncompatibleDataTypeException(Class expectedDataType, Class actualDataType, Exception innerException) {
        super(String.format("Was looking for DataNode of type `%s`, actually found `%s`", expectedDataType.getName(),
                actualDataType.getName()), innerException);
    }

}
