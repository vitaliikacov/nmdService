package unit.feed.controller.importer;

import nmd.orb.services.ImportService;
import org.junit.After;
import org.junit.Before;
import unit.feed.controller.stub.FeedImportJobRepositoryStub;
import unit.feed.controller.stub.TransactionsStub;

/**
 * @author : igu
 */
public abstract class AbstractImportServiceTest {

    protected FeedImportJobRepositoryStub feedImportJobRepositoryStub;
    protected ImportService importService;

    private TransactionsStub transactionsStub;

    @Before
    public void setUp() {
        this.transactionsStub = new TransactionsStub();

        this.feedImportJobRepositoryStub = new FeedImportJobRepositoryStub();

        this.importService = new ImportService(this.feedImportJobRepositoryStub, this.transactionsStub);
    }

    @After
    public void tearDown() {
        this.transactionsStub.assertOpenedTransactionNotActive();
    }

}
