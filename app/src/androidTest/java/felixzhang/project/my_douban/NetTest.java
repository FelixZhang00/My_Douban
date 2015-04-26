package felixzhang.project.my_douban;


import android.test.InstrumentationTestCase;

import java.io.IOException;

import felixzhang.project.my_douban.engine.DoubanFetcher;

/**
 * Created by felix on 15/4/26.
 */
public class NetTest extends InstrumentationTestCase{

    public void testLogin() throws IOException {
        DoubanFetcher fetcher=new DoubanFetcher();
        fetcher.login("344087491@qq.com","998348**");

    }

}
