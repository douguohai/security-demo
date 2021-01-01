package com.example.securitydemo.bean;

/**
 * @version : 1.0
 * @description: java类作用描述
 * @author: tianwen
 * @create: 2021/1/1 15:50
 **/
import io.gitee.tooleek.lock.spring.boot.config.LockConfig;
import io.gitee.tooleek.lock.spring.boot.config.LockConfig.ClusterConfig;
import io.gitee.tooleek.lock.spring.boot.config.LockConfig.MasterSlaveConfig;
import io.gitee.tooleek.lock.spring.boot.config.LockConfig.ReplicatedConfig;
import io.gitee.tooleek.lock.spring.boot.config.LockConfig.SentinelConfig;
import io.gitee.tooleek.lock.spring.boot.config.LockConfig.SingleConfig;
import io.gitee.tooleek.lock.spring.boot.core.LockInterceptor;
import io.gitee.tooleek.lock.spring.boot.enumeration.ServerPattern;
import io.gitee.tooleek.lock.spring.boot.exception.UnknownLoadBalancerException;
import io.gitee.tooleek.lock.spring.boot.exception.UnknownReadModeException;
import io.gitee.tooleek.lock.spring.boot.exception.UnknownSubscriptionModeException;
import io.gitee.tooleek.lock.spring.boot.factory.ServiceBeanFactory;
import io.gitee.tooleek.lock.spring.boot.service.impl.FairLockServiceImpl;
import io.gitee.tooleek.lock.spring.boot.service.impl.MultiLockServiceImpl;
import io.gitee.tooleek.lock.spring.boot.service.impl.ReadLockServiceImpl;
import io.gitee.tooleek.lock.spring.boot.service.impl.RedLockServiceImpl;
import io.gitee.tooleek.lock.spring.boot.service.impl.ReentrantLockServiceImpl;
import io.gitee.tooleek.lock.spring.boot.service.impl.WriteLockServiceImpl;
import io.gitee.tooleek.lock.spring.boot.store.MapStore;
import io.gitee.tooleek.lock.spring.boot.util.SpringUtil;
import io.gitee.tooleek.lock.spring.boot.util.ValidateUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.BaseMasterSlaveServersConfig;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.ReadMode;
import org.redisson.config.ReplicatedServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.SslProvider;
import org.redisson.config.SubscriptionMode;
import org.redisson.connection.balancer.LoadBalancer;
import org.redisson.connection.balancer.RandomLoadBalancer;
import org.redisson.connection.balancer.RoundRobinLoadBalancer;
import org.redisson.connection.balancer.WeightedRoundRobinBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

//@Configuration
//@EnableConfigurationProperties({LockConfig.class})
//@Import({LockInterceptor.class})
public class LockAutoConfiguration {
    @Autowired
    private LockConfig lockConfig;

    public LockAutoConfiguration() {
    }

    @Bean(
            name = {"lockRedissonClient"},
            destroyMethod = "shutdown"
    )
    public RedissonClient redissonClient() throws URISyntaxException {
        Config config = new Config();
        ServerPattern serverPattern = MapStore.getServerPattern(this.lockConfig.getPattern());
        if (serverPattern == ServerPattern.SINGLE) {
            SingleServerConfig singleServerConfig = config.useSingleServer();
            this.initSingleConfig(singleServerConfig);
        }

        if (serverPattern == ServerPattern.CLUSTER) {
            ClusterServersConfig clusterConfig = config.useClusterServers();
            this.initClusterConfig(clusterConfig);
        }

        if (serverPattern == ServerPattern.MASTER_SLAVE) {
            MasterSlaveServersConfig masterSlaveConfig = config.useMasterSlaveServers();
            this.initMasterSlaveConfig(masterSlaveConfig);
        }

        if (serverPattern == ServerPattern.REPLICATED) {
            ReplicatedServersConfig replicatedServersConfig = config.useReplicatedServers();
            this.initReplicatedServersConfig(replicatedServersConfig);
        }

        if (serverPattern == ServerPattern.SENTINEL) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            this.initSentinelServersConfig(sentinelServersConfig);
        }

        return Redisson.create(config);
    }

    @Bean
    public ServiceBeanFactory serviceBeanFactory() {
        return new ServiceBeanFactory();
    }

    @Bean
    public SpringUtil springUtil() {
        return new SpringUtil();
    }

    @Bean
    @Scope("prototype")
    public ReentrantLockServiceImpl reentrantLockServiceImpl() {
        return new ReentrantLockServiceImpl();
    }

    @Bean
    @Scope("prototype")
    public FairLockServiceImpl fairLockServiceImpl() {
        return new FairLockServiceImpl();
    }

    @Bean
    @Scope("prototype")
    public MultiLockServiceImpl multiLockServiceImpl() {
        return new MultiLockServiceImpl();
    }

    @Bean
    @Scope("prototype")
    public RedLockServiceImpl redLockServiceImpl() {
        return new RedLockServiceImpl();
    }

    @Bean
    @Scope("prototype")
    public ReadLockServiceImpl readLockServiceImpl() {
        return new ReadLockServiceImpl();
    }

    @Bean
    @Scope("prototype")
    public WriteLockServiceImpl writeLockServiceImpl() {
        return new WriteLockServiceImpl();
    }

    private void initSingleConfig(SingleServerConfig singleServerConfig) throws URISyntaxException {
        SingleConfig singleConfig = this.lockConfig.getSingleServer();
        singleServerConfig.setAddress(String.format("%s%s%s%s", "redis://", singleConfig.getAddress(), ":", singleConfig.getPort()));
        singleServerConfig.setClientName(this.lockConfig.getClientName());
        singleServerConfig.setConnectionMinimumIdleSize(singleConfig.getConnMinIdleSize());
        singleServerConfig.setConnectionPoolSize(singleConfig.getConnPoolSize());
        singleServerConfig.setConnectTimeout(singleConfig.getConnTimeout());
        singleServerConfig.setDatabase(singleConfig.getDatabase());
        singleServerConfig.setDnsMonitoringInterval((long)singleConfig.getDnsMonitoringInterval());
        singleServerConfig.setIdleConnectionTimeout(singleConfig.getIdleConnTimeout());
        singleServerConfig.setKeepAlive(singleConfig.isKeepAlive());
        singleServerConfig.setPassword(singleConfig.getPassword());
        singleServerConfig.setRetryAttempts(singleConfig.getRetryAttempts());
        singleServerConfig.setRetryInterval(singleConfig.getRetryInterval());
        singleServerConfig.setSslEnableEndpointIdentification(this.lockConfig.isSslEnableEndpointIdentification());
        if (this.lockConfig.getSslKeystore() != null) {
            singleServerConfig.setSslKeystore(new URI(this.lockConfig.getSslKeystore()));
        }

        if (this.lockConfig.getSslKeystorePassword() != null) {
            singleServerConfig.setSslKeystorePassword(this.lockConfig.getSslKeystorePassword());
        }

        singleServerConfig.setSslProvider("JDK".equalsIgnoreCase(this.lockConfig.getSslProvider()) ? SslProvider.JDK : SslProvider.OPENSSL);
    }

    private void initClusterConfig(ClusterServersConfig clusterServerConfig) {
        ClusterConfig clusterConfig = this.lockConfig.getClusterServer();
        String[] addressArr = clusterConfig.getNodeAddresses().split(",");
        Arrays.asList(addressArr).forEach((address) -> {
            clusterServerConfig.addNodeAddress(new String[]{String.format("%s%s", "redis://", address)});
        });
        clusterServerConfig.setScanInterval(clusterConfig.getScanInterval());
        ReadMode readMode = this.getReadMode(clusterConfig.getReadMode());
        ValidateUtil.notNull(readMode, UnknownReadModeException.class, "未知读取操作的负载均衡模式类型");
        clusterServerConfig.setReadMode(readMode);
        SubscriptionMode subscriptionMode = this.getSubscriptionMode(clusterConfig.getSubMode());
        ValidateUtil.notNull(subscriptionMode, UnknownSubscriptionModeException.class, "未知订阅操作的负载均衡模式类型");
        clusterServerConfig.setSubscriptionMode(subscriptionMode);
        LoadBalancer loadBalancer = this.getLoadBalancer(clusterConfig.getLoadBalancer(), clusterConfig.getWeightMaps(), clusterConfig.getDefaultWeight());
        ValidateUtil.notNull(loadBalancer, UnknownLoadBalancerException.class, "未知负载均衡算法类型");
        clusterServerConfig.setLoadBalancer(loadBalancer);
        clusterServerConfig.setSubscriptionConnectionMinimumIdleSize(clusterConfig.getSubConnMinIdleSize());
        clusterServerConfig.setSubscriptionConnectionPoolSize(clusterConfig.getSubConnPoolSize());
        clusterServerConfig.setSlaveConnectionMinimumIdleSize(clusterConfig.getSlaveConnMinIdleSize());
        clusterServerConfig.setSlaveConnectionPoolSize(clusterConfig.getSlaveConnPoolSize());
        clusterServerConfig.setMasterConnectionMinimumIdleSize(clusterConfig.getMasterConnMinIdleSize());
        clusterServerConfig.setMasterConnectionPoolSize(clusterConfig.getMasterConnPoolSize());
        clusterServerConfig.setIdleConnectionTimeout(clusterConfig.getIdleConnTimeout());
        clusterServerConfig.setConnectTimeout(clusterConfig.getConnTimeout());
        clusterServerConfig.setTimeout(clusterConfig.getTimeout());
        clusterServerConfig.setRetryAttempts(clusterConfig.getRetryAttempts());
        clusterServerConfig.setRetryInterval(clusterConfig.getRetryInterval());
        clusterServerConfig.setPassword(clusterConfig.getPassword());
        clusterServerConfig.setSubscriptionsPerConnection(clusterConfig.getSubPerConn());
        clusterServerConfig.setClientName(this.lockConfig.getClientName());
    }

    private void initSentinelServersConfig(SentinelServersConfig sentinelServersConfig) throws URISyntaxException {
        SentinelConfig sentinelConfig = this.lockConfig.getSentinelServer();
        String[] addressArr = sentinelConfig.getSentinelAddresses().split(",");
        Arrays.asList(addressArr).forEach((address) -> {
            sentinelServersConfig.addSentinelAddress(new String[]{String.format("%s%s", "redis://", address)});
        });
        ReadMode readMode = this.getReadMode(sentinelConfig.getReadMode());
        ValidateUtil.notNull(readMode, UnknownReadModeException.class, "未知读取操作的负载均衡模式类型");
        sentinelServersConfig.setReadMode(readMode);
        SubscriptionMode subscriptionMode = this.getSubscriptionMode(sentinelConfig.getSubMode());
        ValidateUtil.notNull(subscriptionMode, UnknownSubscriptionModeException.class, "未知订阅操作的负载均衡模式类型");
        sentinelServersConfig.setSubscriptionMode(subscriptionMode);
        LoadBalancer loadBalancer = this.getLoadBalancer(sentinelConfig.getLoadBalancer(), sentinelConfig.getWeightMaps(), sentinelConfig.getDefaultWeight());
        ValidateUtil.notNull(loadBalancer, UnknownLoadBalancerException.class, "未知负载均衡算法类型");
        sentinelServersConfig.setLoadBalancer(loadBalancer);
        sentinelServersConfig.setMasterName(sentinelConfig.getMasterName());
        sentinelServersConfig.setDatabase(sentinelConfig.getDatabase());
        sentinelServersConfig.setSlaveConnectionPoolSize(sentinelConfig.getSlaveConnectionPoolSize());
        sentinelServersConfig.setMasterConnectionPoolSize(sentinelConfig.getMasterConnectionPoolSize());
        sentinelServersConfig.setSubscriptionConnectionPoolSize(sentinelConfig.getSubscriptionConnectionPoolSize());
        sentinelServersConfig.setSlaveConnectionMinimumIdleSize(sentinelConfig.getSlaveConnectionMinimumIdleSize());
        sentinelServersConfig.setMasterConnectionMinimumIdleSize(sentinelConfig.getMasterConnectionMinimumIdleSize());
        sentinelServersConfig.setSubscriptionConnectionMinimumIdleSize(sentinelConfig.getSubscriptionConnectionMinimumIdleSize());
        sentinelServersConfig.setDnsMonitoringInterval(sentinelConfig.getDnsMonitoringInterval());
        sentinelServersConfig.setSubscriptionsPerConnection(sentinelConfig.getSubscriptionsPerConnection());
        sentinelServersConfig.setPassword(sentinelConfig.getPassword());
        sentinelServersConfig.setRetryAttempts(sentinelConfig.getRetryAttempts());
        sentinelServersConfig.setRetryInterval(sentinelConfig.getRetryInterval());
        sentinelServersConfig.setTimeout(sentinelConfig.getTimeout());
        sentinelServersConfig.setConnectTimeout(sentinelConfig.getConnectTimeout());
        sentinelServersConfig.setIdleConnectionTimeout(sentinelConfig.getIdleConnectionTimeout());
        this.setLockSslConfigAndClientName(sentinelServersConfig);
    }

    private void initReplicatedServersConfig(ReplicatedServersConfig replicatedServersConfig) throws URISyntaxException {
        ReplicatedConfig replicatedConfig = this.lockConfig.getReplicatedServer();
        String[] addressArr = replicatedConfig.getNodeAddresses().split(",");
        Arrays.asList(addressArr).forEach((address) -> {
            replicatedServersConfig.addNodeAddress(new String[]{String.format("%s%s", "redis://", address)});
        });
        ReadMode readMode = this.getReadMode(replicatedConfig.getReadMode());
        ValidateUtil.notNull(readMode, UnknownReadModeException.class, "未知读取操作的负载均衡模式类型");
        replicatedServersConfig.setReadMode(readMode);
        SubscriptionMode subscriptionMode = this.getSubscriptionMode(replicatedConfig.getSubscriptionMode());
        ValidateUtil.notNull(subscriptionMode, UnknownSubscriptionModeException.class, "未知订阅操作的负载均衡模式类型");
        replicatedServersConfig.setSubscriptionMode(subscriptionMode);
        LoadBalancer loadBalancer = this.getLoadBalancer(replicatedConfig.getLoadBalancer(), replicatedConfig.getWeightMaps(), replicatedConfig.getDefaultWeight());
        ValidateUtil.notNull(loadBalancer, UnknownLoadBalancerException.class, "未知负载均衡算法类型");
        replicatedServersConfig.setLoadBalancer(loadBalancer);
        replicatedServersConfig.setScanInterval(replicatedConfig.getScanInterval());
        replicatedServersConfig.setDatabase(replicatedConfig.getDatabase());
        replicatedServersConfig.setSlaveConnectionPoolSize(replicatedConfig.getSlaveConnectionPoolSize());
        replicatedServersConfig.setMasterConnectionPoolSize(replicatedConfig.getMasterConnectionPoolSize());
        replicatedServersConfig.setSubscriptionConnectionPoolSize(replicatedConfig.getSubscriptionConnectionPoolSize());
        replicatedServersConfig.setSlaveConnectionMinimumIdleSize(replicatedConfig.getSlaveConnectionMinimumIdleSize());
        replicatedServersConfig.setMasterConnectionMinimumIdleSize(replicatedConfig.getMasterConnectionMinimumIdleSize());
        replicatedServersConfig.setSubscriptionConnectionMinimumIdleSize(replicatedConfig.getSubscriptionConnectionMinimumIdleSize());
        replicatedServersConfig.setDnsMonitoringInterval(replicatedConfig.getDnsMonitoringInterval());
        replicatedServersConfig.setSubscriptionsPerConnection(replicatedConfig.getSubscriptionsPerConnection());
        replicatedServersConfig.setPassword(replicatedConfig.getPassword());
        replicatedServersConfig.setRetryAttempts(replicatedConfig.getRetryAttempts());
        replicatedServersConfig.setRetryInterval(replicatedConfig.getRetryInterval());
        replicatedServersConfig.setTimeout(replicatedConfig.getTimeout());
        replicatedServersConfig.setConnectTimeout(replicatedConfig.getConnectTimeout());
        replicatedServersConfig.setIdleConnectionTimeout(replicatedConfig.getIdleConnectionTimeout());
        this.setLockSslConfigAndClientName(replicatedServersConfig);
    }

    private void initMasterSlaveConfig(MasterSlaveServersConfig masterSlaveServersConfig) throws URISyntaxException {
        MasterSlaveConfig masterSlaveConfig = this.lockConfig.getMasterSlaveServer();
        masterSlaveServersConfig.setMasterAddress(String.format("%s%s", "redis://", masterSlaveConfig.getMasterAddress()));
        String[] addressArr = masterSlaveConfig.getSlaveAddresses().split(",");
        Arrays.asList(addressArr).forEach((address) -> {
            masterSlaveServersConfig.addSlaveAddress(new String[]{String.format("%s%s", "redis://", address)});
        });
        ReadMode readMode = this.getReadMode(masterSlaveConfig.getReadMode());
        ValidateUtil.notNull(readMode, UnknownReadModeException.class, "未知读取操作的负载均衡模式类型");
        masterSlaveServersConfig.setReadMode(readMode);
        SubscriptionMode subscriptionMode = this.getSubscriptionMode(masterSlaveConfig.getSubMode());
        ValidateUtil.notNull(subscriptionMode, UnknownSubscriptionModeException.class, "未知订阅操作的负载均衡模式类型");
        masterSlaveServersConfig.setSubscriptionMode(subscriptionMode);
        LoadBalancer loadBalancer = this.getLoadBalancer(masterSlaveConfig.getLoadBalancer(), masterSlaveConfig.getWeightMaps(), masterSlaveConfig.getDefaultWeight());
        ValidateUtil.notNull(loadBalancer, UnknownLoadBalancerException.class, "未知负载均衡算法类型");
        masterSlaveServersConfig.setLoadBalancer(loadBalancer);
        masterSlaveServersConfig.setDatabase(masterSlaveConfig.getDatabase());
        masterSlaveServersConfig.setSlaveConnectionPoolSize(masterSlaveConfig.getSlaveConnectionPoolSize());
        masterSlaveServersConfig.setMasterConnectionPoolSize(masterSlaveConfig.getMasterConnectionPoolSize());
        masterSlaveServersConfig.setSubscriptionConnectionPoolSize(masterSlaveConfig.getSubscriptionConnectionPoolSize());
        masterSlaveServersConfig.setSlaveConnectionMinimumIdleSize(masterSlaveConfig.getSlaveConnectionMinimumIdleSize());
        masterSlaveServersConfig.setMasterConnectionMinimumIdleSize(masterSlaveConfig.getMasterConnectionMinimumIdleSize());
        masterSlaveServersConfig.setSubscriptionConnectionMinimumIdleSize(masterSlaveConfig.getSubscriptionConnectionMinimumIdleSize());
        masterSlaveServersConfig.setDnsMonitoringInterval(masterSlaveConfig.getDnsMonitoringInterval());
        masterSlaveServersConfig.setSubscriptionsPerConnection(masterSlaveConfig.getSubscriptionsPerConnection());
        masterSlaveServersConfig.setPassword(masterSlaveConfig.getPassword());
        masterSlaveServersConfig.setRetryAttempts(masterSlaveConfig.getRetryAttempts());
        masterSlaveServersConfig.setRetryInterval(masterSlaveConfig.getRetryInterval());
        masterSlaveServersConfig.setTimeout(masterSlaveConfig.getTimeout());
        masterSlaveServersConfig.setConnectTimeout(masterSlaveConfig.getConnectTimeout());
        masterSlaveServersConfig.setIdleConnectionTimeout(masterSlaveConfig.getIdleConnectionTimeout());
        this.setLockSslConfigAndClientName(masterSlaveServersConfig);
    }

    private LoadBalancer getLoadBalancer(String loadBalancerType, String customerWeightMaps, int defaultWeight) {
        if ("RandomLoadBalancer".equals(loadBalancerType)) {
            return new RandomLoadBalancer();
        } else if ("RoundRobinLoadBalancer".equals(loadBalancerType)) {
            return new RoundRobinLoadBalancer();
        } else if ("WeightedRoundRobinBalancer".equals(loadBalancerType)) {
            Map<String, Integer> weights = new HashMap(16);
            String[] weightMaps = customerWeightMaps.split(";");
            Arrays.asList(weightMaps).forEach((weightMap) -> {
                Integer var10000 = (Integer)weights.put("redis://" + weightMap.split(",")[0], Integer.parseInt(weightMap.split(",")[1]));
            });
            return new WeightedRoundRobinBalancer(weights, defaultWeight);
        } else {
            return null;
        }
    }

    private ReadMode getReadMode(String readModeType) {
        if ("SLAVE".equals(readModeType)) {
            return ReadMode.SLAVE;
        } else if ("MASTER".equals(readModeType)) {
            return ReadMode.MASTER;
        } else {
            return "MASTER_SLAVE".equals(readModeType) ? ReadMode.MASTER_SLAVE : null;
        }
    }

    private SubscriptionMode getSubscriptionMode(String subscriptionModeType) {
        if ("SLAVE".equals(subscriptionModeType)) {
            return SubscriptionMode.SLAVE;
        } else {
            return "MASTER".equals(subscriptionModeType) ? SubscriptionMode.MASTER : null;
        }
    }

    private <T extends BaseMasterSlaveServersConfig> void setLockSslConfigAndClientName(T lockAutoConfig) throws URISyntaxException {
        lockAutoConfig.setClientName(this.lockConfig.getClientName());
        lockAutoConfig.setSslEnableEndpointIdentification(this.lockConfig.isSslEnableEndpointIdentification());
        if (this.lockConfig.getSslKeystore() != null) {
            lockAutoConfig.setSslKeystore(new URI(this.lockConfig.getSslKeystore()));
        }

        if (this.lockConfig.getSslKeystorePassword() != null) {
            lockAutoConfig.setSslKeystorePassword(this.lockConfig.getSslKeystorePassword());
        }

        if (this.lockConfig.getSslTruststore() != null) {
            lockAutoConfig.setSslTruststore(new URI(this.lockConfig.getSslTruststore()));
        }

        if (this.lockConfig.getSslTruststorePassword() != null) {
            lockAutoConfig.setSslTruststorePassword(this.lockConfig.getSslTruststorePassword());
        }

        lockAutoConfig.setSslProvider("JDK".equalsIgnoreCase(this.lockConfig.getSslProvider()) ? SslProvider.JDK : SslProvider.OPENSSL);
    }
}

