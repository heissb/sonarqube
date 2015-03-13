// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: constants.proto

package org.sonar.batch.protocol;

public final class Constants {
  private Constants() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  /**
   * Protobuf enum {@code Severity}
   */
  public enum Severity
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>INFO = 0;</code>
     */
    INFO(0, 0),
    /**
     * <code>MINOR = 1;</code>
     */
    MINOR(1, 1),
    /**
     * <code>MAJOR = 2;</code>
     */
    MAJOR(2, 2),
    /**
     * <code>CRITICAL = 3;</code>
     */
    CRITICAL(3, 3),
    /**
     * <code>BLOCKER = 4;</code>
     */
    BLOCKER(4, 4),
    ;

    /**
     * <code>INFO = 0;</code>
     */
    public static final int INFO_VALUE = 0;
    /**
     * <code>MINOR = 1;</code>
     */
    public static final int MINOR_VALUE = 1;
    /**
     * <code>MAJOR = 2;</code>
     */
    public static final int MAJOR_VALUE = 2;
    /**
     * <code>CRITICAL = 3;</code>
     */
    public static final int CRITICAL_VALUE = 3;
    /**
     * <code>BLOCKER = 4;</code>
     */
    public static final int BLOCKER_VALUE = 4;


    public final int getNumber() { return value; }

    public static Severity valueOf(int value) {
      switch (value) {
        case 0: return INFO;
        case 1: return MINOR;
        case 2: return MAJOR;
        case 3: return CRITICAL;
        case 4: return BLOCKER;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Severity>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<Severity>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Severity>() {
            public Severity findValueByNumber(int number) {
              return Severity.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return org.sonar.batch.protocol.Constants.getDescriptor().getEnumTypes().get(0);
    }

    private static final Severity[] VALUES = values();

    public static Severity valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private Severity(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:Severity)
  }

  /**
   * Protobuf enum {@code ComponentType}
   */
  public enum ComponentType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>PROJECT = 0;</code>
     */
    PROJECT(0, 0),
    /**
     * <code>MODULE = 1;</code>
     */
    MODULE(1, 1),
    /**
     * <code>DIRECTORY = 2;</code>
     */
    DIRECTORY(2, 2),
    /**
     * <code>FILE = 3;</code>
     */
    FILE(3, 3),
    /**
     * <code>VIEW = 4;</code>
     */
    VIEW(4, 4),
    /**
     * <code>SUBVIEW = 5;</code>
     */
    SUBVIEW(5, 5),
    ;

    /**
     * <code>PROJECT = 0;</code>
     */
    public static final int PROJECT_VALUE = 0;
    /**
     * <code>MODULE = 1;</code>
     */
    public static final int MODULE_VALUE = 1;
    /**
     * <code>DIRECTORY = 2;</code>
     */
    public static final int DIRECTORY_VALUE = 2;
    /**
     * <code>FILE = 3;</code>
     */
    public static final int FILE_VALUE = 3;
    /**
     * <code>VIEW = 4;</code>
     */
    public static final int VIEW_VALUE = 4;
    /**
     * <code>SUBVIEW = 5;</code>
     */
    public static final int SUBVIEW_VALUE = 5;


    public final int getNumber() { return value; }

    public static ComponentType valueOf(int value) {
      switch (value) {
        case 0: return PROJECT;
        case 1: return MODULE;
        case 2: return DIRECTORY;
        case 3: return FILE;
        case 4: return VIEW;
        case 5: return SUBVIEW;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<ComponentType>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<ComponentType>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ComponentType>() {
            public ComponentType findValueByNumber(int number) {
              return ComponentType.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return org.sonar.batch.protocol.Constants.getDescriptor().getEnumTypes().get(1);
    }

    private static final ComponentType[] VALUES = values();

    public static ComponentType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private ComponentType(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:ComponentType)
  }

  /**
   * Protobuf enum {@code EventCategory}
   *
   * <pre>
   * temporary enum during development of computation stack
   * </pre>
   */
  public enum EventCategory
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>ALERT = 0;</code>
     */
    ALERT(0, 0),
    /**
     * <code>PROFILE = 1;</code>
     */
    PROFILE(1, 1),
    ;

    /**
     * <code>ALERT = 0;</code>
     */
    public static final int ALERT_VALUE = 0;
    /**
     * <code>PROFILE = 1;</code>
     */
    public static final int PROFILE_VALUE = 1;


    public final int getNumber() { return value; }

    public static EventCategory valueOf(int value) {
      switch (value) {
        case 0: return ALERT;
        case 1: return PROFILE;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<EventCategory>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<EventCategory>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<EventCategory>() {
            public EventCategory findValueByNumber(int number) {
              return EventCategory.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return org.sonar.batch.protocol.Constants.getDescriptor().getEnumTypes().get(2);
    }

    private static final EventCategory[] VALUES = values();

    public static EventCategory valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private EventCategory(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:EventCategory)
  }

  /**
   * Protobuf enum {@code ComponentLinkType}
   */
  public enum ComponentLinkType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>HOME = 0;</code>
     */
    HOME(0, 0),
    /**
     * <code>SCM = 1;</code>
     */
    SCM(1, 1),
    /**
     * <code>SCM_DEV = 2;</code>
     */
    SCM_DEV(2, 2),
    /**
     * <code>ISSUE = 3;</code>
     */
    ISSUE(3, 3),
    /**
     * <code>CI = 4;</code>
     */
    CI(4, 4),
    ;

    /**
     * <code>HOME = 0;</code>
     */
    public static final int HOME_VALUE = 0;
    /**
     * <code>SCM = 1;</code>
     */
    public static final int SCM_VALUE = 1;
    /**
     * <code>SCM_DEV = 2;</code>
     */
    public static final int SCM_DEV_VALUE = 2;
    /**
     * <code>ISSUE = 3;</code>
     */
    public static final int ISSUE_VALUE = 3;
    /**
     * <code>CI = 4;</code>
     */
    public static final int CI_VALUE = 4;


    public final int getNumber() { return value; }

    public static ComponentLinkType valueOf(int value) {
      switch (value) {
        case 0: return HOME;
        case 1: return SCM;
        case 2: return SCM_DEV;
        case 3: return ISSUE;
        case 4: return CI;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<ComponentLinkType>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<ComponentLinkType>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ComponentLinkType>() {
            public ComponentLinkType findValueByNumber(int number) {
              return ComponentLinkType.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return org.sonar.batch.protocol.Constants.getDescriptor().getEnumTypes().get(3);
    }

    private static final ComponentLinkType[] VALUES = values();

    public static ComponentLinkType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private ComponentLinkType(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:ComponentLinkType)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017constants.proto*E\n\010Severity\022\010\n\004INFO\020\000\022" +
      "\t\n\005MINOR\020\001\022\t\n\005MAJOR\020\002\022\014\n\010CRITICAL\020\003\022\013\n\007B" +
      "LOCKER\020\004*X\n\rComponentType\022\013\n\007PROJECT\020\000\022\n" +
      "\n\006MODULE\020\001\022\r\n\tDIRECTORY\020\002\022\010\n\004FILE\020\003\022\010\n\004V" +
      "IEW\020\004\022\013\n\007SUBVIEW\020\005*\'\n\rEventCategory\022\t\n\005A" +
      "LERT\020\000\022\013\n\007PROFILE\020\001*F\n\021ComponentLinkType" +
      "\022\010\n\004HOME\020\000\022\007\n\003SCM\020\001\022\013\n\007SCM_DEV\020\002\022\t\n\005ISSU" +
      "E\020\003\022\006\n\002CI\020\004B\034\n\030org.sonar.batch.protocolH" +
      "\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
