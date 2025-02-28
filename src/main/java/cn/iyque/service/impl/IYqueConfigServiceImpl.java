package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueConfigDao;
import cn.iyque.entity.IYqueConfig;
import cn.iyque.service.IYqueConfigService;
import me.chanjar.weixin.cp.api.WxCpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class IYqueConfigServiceImpl implements IYqueConfigService {

    @Autowired
    IYqueConfigDao iYqueConfigDao;
    @Override
    public IYqueConfig findIYqueConfig() {
        List<IYqueConfig> iYqueConfigs = iYqueConfigDao.findAll();
        if(CollectionUtil.isEmpty(iYqueConfigs)){
            return new IYqueConfig();
        }
        IYqueConfig iYqueConfig = iYqueConfigs.stream().findFirst().get();
        iYqueConfig.setMsgAuditLibPath("/usr/local/app/scrm/sdk/libWeWorkFinanceSdk_Java.so");
        iYqueConfig.setMsgAuditSecret("hu-jG8X72WricZEkvx3U-02j7aYsANCFAmmAKkc5568");
        iYqueConfig.setMsgAuditPriKey("-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEowIBAAKCAQEAhGy1E+4gx6s/zbOmsaCZGn/ZkHz6k9gnfZtrvTiY0obWLiNR\n" +
                "srYaa3cuRQCsEE/Je3J6uh3W0mJoWSCLN3z7OY3FGH/hFaYJlMDBcdZac5XD3iXq\n" +
                "83uR57MSSU5EFU3IzhhvJQ8w6Sgq9/ujPEP5xZGEtMpqsvUBvK2A1HMAD51TtnW0\n" +
                "Q/+dzAAz3FOjnwiTgZgCr5wLH+t2vdi6GUz+XJFCgf5DHEod1sTIL2U1TH3m/clw\n" +
                "hBQxoI42gpxlB3TWR+sy3iT3dkQpebKrMObz69eB3OPeAwZW/ckrHS6dxODmani7\n" +
                "HIRZ0//8NYnmkQVoLxKAMTjGYoXC5nNasXfOsQIDAQABAoIBAAElnmNh7X2BO5Qc\n" +
                "5VmWQCE5QIGGfZaceYoWV6ZryDIRmicL5gQf3OZ3oapWFUduY3q63PqB8eeAU/JK\n" +
                "rH4CanwfANkjmWfaOGzGBx7k6gNkhY3l4u0xMawxsju5jnAnTHERh9vor+YjE40y\n" +
                "jCFEXvRRvHYi5y3t3pIbaBle9h4MffMZfFEeTroqi7nplYva5azkbgTfAvBr11bi\n" +
                "H22O0r+qbCv4XTq36ZHV/7U0Ll+oY2tN7aP8MY8q7rQJHSLkS8VtuuX+cuLnDwBi\n" +
                "sVm5p6vI3cHh/hJQUVaYC6Sx/QLjfMfbSQNEG303lPYvGzEDnR2SmyuoY036eo9b\n" +
                "WX+918kCgYEA0Sp3hm7BDZH8NWjeLfyOKBT0kNuS1FcWwepxCItritvVU84cZrGB\n" +
                "1KnhPhh38QMj3xEI0hQ7TAs7UNU7hCvxgeyCyi70aS1dqJMpWv1tlLa4iLmlRf0O\n" +
                "SEWwW0FB9rWAGEwu0G2LGm3j3C6joWREEfliebLDfhIxC0DP9Ym9gmkCgYEAohNh\n" +
                "CuPKGvpbiW9Q8mumjDeKvGPHdAnRYBr2WgjkE0Z6Ra6CqnzfC1UcHtQsFxcAz4Td\n" +
                "ggGm7aaw7tMQLykAzb6SQs/X7ni7QCaMI2tzx/reWSUpTfouG/NmVWqNAN1uJWaz\n" +
                "gpd3o3zaeuWNFZih7wPryUOh5Gu22W7+u0/TUQkCgYAKUK3UU2Hx6WEmwSUz32Cj\n" +
                "WWU2P53eGu1kYstwQG/tSSocF+4OonJkqnwRM+O2XxFFzvFAav8BoQwr0Fg/DGu8\n" +
                "nI1WqzoidbpItfk0I4tj6h7vnzgnW3N43Pb1iSJXqFvI1vj4Dm7tvr2d/i7Tpr40\n" +
                "pLfWiJpbfUot4+Rdl41hAQKBgQChp8CYEwGnjOS2HOBZG0RgAJoRM1hiBdTfDaV/\n" +
                "wfRywU39qITNtYTmkbpdiXR8h0YYtcYWAL3VtfguDTjMQRJ7JERP/Wy7f5IM9yW5\n" +
                "wlgWM8OImMcs7ocE++2q7n67SSCuj1QmwhrjZwmnoMPqsXV6p6eK9DlKUqFrGAld\n" +
                "HM6BuQKBgFTII7NqZliiDizmQMlFlIIe86/fI928eXaRLROaYsb4a41cSidt1occ\n" +
                "6nf+UdmW7Dd0MCpFzFYP2Eqk1ZuuYXuPnoLLeucaTEcz8GKTWpCAE8pLd7yM6mq9\n" +
                "JN/K3nzz3HpSFOMcik7tUB1U5JNbhq6OkCVWqFJvfFqiLhByVjbV\n" +
                "-----END RSA PRIVATE KEY-----");
        return iYqueConfig;
    }

    @Override
    public void saveOrUpdate(IYqueConfig iYqueConfig) {
        iYqueConfigDao.saveAndFlush(iYqueConfig);
    }

    @Override
    public WxCpService findWxcpservice() throws Exception {

        WxCpService config = WxCpServiceFactory.createWxCpService(findIYqueConfig());
        if(null == config){
            throw new Exception("请配置系统应用参数");
        }

        return config;
    }
}
