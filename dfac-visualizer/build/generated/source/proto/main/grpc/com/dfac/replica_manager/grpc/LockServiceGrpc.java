package com.dfac.replica_manager.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.60.0)",
    comments = "Source: lock_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class LockServiceGrpc {

  private LockServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.dfac.replica_manager.LockService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.dfac.replica_manager.grpc.LockRequest,
      com.dfac.replica_manager.grpc.LockResponse> getRequestLockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestLock",
      requestType = com.dfac.replica_manager.grpc.LockRequest.class,
      responseType = com.dfac.replica_manager.grpc.LockResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.dfac.replica_manager.grpc.LockRequest,
      com.dfac.replica_manager.grpc.LockResponse> getRequestLockMethod() {
    io.grpc.MethodDescriptor<com.dfac.replica_manager.grpc.LockRequest, com.dfac.replica_manager.grpc.LockResponse> getRequestLockMethod;
    if ((getRequestLockMethod = LockServiceGrpc.getRequestLockMethod) == null) {
      synchronized (LockServiceGrpc.class) {
        if ((getRequestLockMethod = LockServiceGrpc.getRequestLockMethod) == null) {
          LockServiceGrpc.getRequestLockMethod = getRequestLockMethod =
              io.grpc.MethodDescriptor.<com.dfac.replica_manager.grpc.LockRequest, com.dfac.replica_manager.grpc.LockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RequestLock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.dfac.replica_manager.grpc.LockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.dfac.replica_manager.grpc.LockResponse.getDefaultInstance()))
              .setSchemaDescriptor(new LockServiceMethodDescriptorSupplier("RequestLock"))
              .build();
        }
      }
    }
    return getRequestLockMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.dfac.replica_manager.grpc.LockRequest,
      com.dfac.replica_manager.grpc.LockResponse> getReleaseLockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReleaseLock",
      requestType = com.dfac.replica_manager.grpc.LockRequest.class,
      responseType = com.dfac.replica_manager.grpc.LockResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.dfac.replica_manager.grpc.LockRequest,
      com.dfac.replica_manager.grpc.LockResponse> getReleaseLockMethod() {
    io.grpc.MethodDescriptor<com.dfac.replica_manager.grpc.LockRequest, com.dfac.replica_manager.grpc.LockResponse> getReleaseLockMethod;
    if ((getReleaseLockMethod = LockServiceGrpc.getReleaseLockMethod) == null) {
      synchronized (LockServiceGrpc.class) {
        if ((getReleaseLockMethod = LockServiceGrpc.getReleaseLockMethod) == null) {
          LockServiceGrpc.getReleaseLockMethod = getReleaseLockMethod =
              io.grpc.MethodDescriptor.<com.dfac.replica_manager.grpc.LockRequest, com.dfac.replica_manager.grpc.LockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReleaseLock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.dfac.replica_manager.grpc.LockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.dfac.replica_manager.grpc.LockResponse.getDefaultInstance()))
              .setSchemaDescriptor(new LockServiceMethodDescriptorSupplier("ReleaseLock"))
              .build();
        }
      }
    }
    return getReleaseLockMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LockServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LockServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LockServiceStub>() {
        @java.lang.Override
        public LockServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LockServiceStub(channel, callOptions);
        }
      };
    return LockServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LockServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LockServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LockServiceBlockingStub>() {
        @java.lang.Override
        public LockServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LockServiceBlockingStub(channel, callOptions);
        }
      };
    return LockServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LockServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LockServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LockServiceFutureStub>() {
        @java.lang.Override
        public LockServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LockServiceFutureStub(channel, callOptions);
        }
      };
    return LockServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void requestLock(com.dfac.replica_manager.grpc.LockRequest request,
        io.grpc.stub.StreamObserver<com.dfac.replica_manager.grpc.LockResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRequestLockMethod(), responseObserver);
    }

    /**
     */
    default void releaseLock(com.dfac.replica_manager.grpc.LockRequest request,
        io.grpc.stub.StreamObserver<com.dfac.replica_manager.grpc.LockResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReleaseLockMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service LockService.
   */
  public static abstract class LockServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return LockServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service LockService.
   */
  public static final class LockServiceStub
      extends io.grpc.stub.AbstractAsyncStub<LockServiceStub> {
    private LockServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LockServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LockServiceStub(channel, callOptions);
    }

    /**
     */
    public void requestLock(com.dfac.replica_manager.grpc.LockRequest request,
        io.grpc.stub.StreamObserver<com.dfac.replica_manager.grpc.LockResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRequestLockMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void releaseLock(com.dfac.replica_manager.grpc.LockRequest request,
        io.grpc.stub.StreamObserver<com.dfac.replica_manager.grpc.LockResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReleaseLockMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service LockService.
   */
  public static final class LockServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<LockServiceBlockingStub> {
    private LockServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LockServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LockServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.dfac.replica_manager.grpc.LockResponse requestLock(com.dfac.replica_manager.grpc.LockRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRequestLockMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.dfac.replica_manager.grpc.LockResponse releaseLock(com.dfac.replica_manager.grpc.LockRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReleaseLockMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service LockService.
   */
  public static final class LockServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<LockServiceFutureStub> {
    private LockServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LockServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LockServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.dfac.replica_manager.grpc.LockResponse> requestLock(
        com.dfac.replica_manager.grpc.LockRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRequestLockMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.dfac.replica_manager.grpc.LockResponse> releaseLock(
        com.dfac.replica_manager.grpc.LockRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReleaseLockMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_LOCK = 0;
  private static final int METHODID_RELEASE_LOCK = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_LOCK:
          serviceImpl.requestLock((com.dfac.replica_manager.grpc.LockRequest) request,
              (io.grpc.stub.StreamObserver<com.dfac.replica_manager.grpc.LockResponse>) responseObserver);
          break;
        case METHODID_RELEASE_LOCK:
          serviceImpl.releaseLock((com.dfac.replica_manager.grpc.LockRequest) request,
              (io.grpc.stub.StreamObserver<com.dfac.replica_manager.grpc.LockResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRequestLockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.dfac.replica_manager.grpc.LockRequest,
              com.dfac.replica_manager.grpc.LockResponse>(
                service, METHODID_REQUEST_LOCK)))
        .addMethod(
          getReleaseLockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.dfac.replica_manager.grpc.LockRequest,
              com.dfac.replica_manager.grpc.LockResponse>(
                service, METHODID_RELEASE_LOCK)))
        .build();
  }

  private static abstract class LockServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LockServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.dfac.replica_manager.grpc.LockServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LockService");
    }
  }

  private static final class LockServiceFileDescriptorSupplier
      extends LockServiceBaseDescriptorSupplier {
    LockServiceFileDescriptorSupplier() {}
  }

  private static final class LockServiceMethodDescriptorSupplier
      extends LockServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    LockServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (LockServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LockServiceFileDescriptorSupplier())
              .addMethod(getRequestLockMethod())
              .addMethod(getReleaseLockMethod())
              .build();
        }
      }
    }
    return result;
  }
}
