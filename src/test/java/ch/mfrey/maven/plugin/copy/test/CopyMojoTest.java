package ch.mfrey.maven.plugin.copy.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

import ch.mfrey.maven.plugin.copy.CopyMojo;
import ch.mfrey.maven.plugin.copy.Replace;
import ch.mfrey.maven.plugin.copy.Resource;

public class CopyMojoTest {

    private static final String currentLoc = new File(CopyMojoTest.class.getResource("/").getFile()).getAbsolutePath();

    private void cleanup(final File file) {
        if (file != null) {
            try {
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void copy() throws MojoExecutionException, MojoFailureException {
        CopyMojo mojo = new CopyMojo();
        mojo.setShowfiles(false);

        Resource resource = new Resource();
        resource.setId("copy");
        resource.addInclude("folder1/**/*.txt");
        resource.setDirectory(currentLoc);
        resource.addPath(Replace.asReplace("folder1", "copy1"));
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertTrue(new File(currentLoc + "/folder1/folder2/test1.txt").exists());
        Assert.assertTrue(new File(currentLoc + "/copy1/folder2/test1.txt").exists());
    }

    @Test
    public void testCopy() throws MojoExecutionException, MojoFailureException {
        CopyMojo mojo = new CopyMojo();
        mojo.setShowfiles(true);

        Resource resource = new Resource();
        resource.setId("testCopy");
        resource.addInclude("folder1/**/*.txt");
        resource.setDirectory(currentLoc);
        resource.addPath(Replace.asReplace("folder1", "copy1"));
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertTrue(new File(currentLoc + "/folder1/folder2/test1.txt").exists());
        File file = new File(currentLoc + "/copy1/folder2/test1.txt");
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testCopy2() throws MojoExecutionException, MojoFailureException {
        CopyMojo mojo = new CopyMojo();
        mojo.setShowfiles(true);

        Resource resource = new Resource();
        resource.setId("testCopy2");
        resource.addInclude("folder1/**/*.txt");
        resource.setDirectory(currentLoc);
        resource.addPath(Replace.asReplace("folder1/folder2", "copy1/copy2"))
                .addPath(Replace.asReplace("test1", "test2"));
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertTrue(new File(currentLoc + "/folder1/folder2/test1.txt").exists());
        File file = new File(currentLoc + "/copy1/copy2/test2.txt");
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testCopyAndReplace() throws MojoExecutionException, MojoFailureException, IOException {
        CopyMojo mojo = new CopyMojo();
        mojo.setShowfiles(true);

        Resource resource = new Resource();
        resource.setId("testCopyAndReplace");
        resource.addInclude("folder1/**/*.txt");
        resource.setDirectory(currentLoc);
        resource.addPath(Replace.asReplace("folder1/folder2", "copy1/copy2"))
                .addPath(Replace.asReplace("test1", "test2"));
        resource.addReplace(Replace.asReplace("to be modified", "has been modified"));
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertTrue(new File(currentLoc + "/folder1/folder2/test1.txt").exists());
        File file = new File(currentLoc + "/copy1/copy2/test2.txt");
        Assert.assertTrue(file.exists());
        String readFileToString = FileUtils.readFileToString(file, resource.getCharset());
        // file.delete();

        // check if replaced twice
        int idx = readFileToString.indexOf("has been modified");
        Assert.assertTrue(idx != -1);
        Assert.assertTrue(readFileToString.indexOf("has been modified", idx + 1) != -1);
    }

    @Test
    public void testCopyAndReplaceRegex() throws MojoExecutionException, MojoFailureException, IOException {
        CopyMojo mojo = new CopyMojo();
        mojo.setShowfiles(true);

        Resource resource = new Resource();
        resource.setId("testCopyAndReplaceRegex");
        resource.addInclude("folder1/**/*.txt");
        resource.setDirectory(currentLoc);
        resource.addPath(
                Replace.asRegex("folder1/([^/]+)", "copy1/copy$1"))
                .addPath(Replace.asReplace("test1", "test2"));
        resource.addReplace(Replace.asRegex("to be m([^d]+)d", "has been m$1d"));
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertTrue(new File(currentLoc + "/folder1/folder2/test1.txt").exists());
        File file = new File(currentLoc + "/copy1/copyfolder2/test2.txt");
        Assert.assertTrue(file.exists());
        String readFileToString = FileUtils.readFileToString(file, resource.getCharset());
        // file.delete();

        // check if replaced twice
        int idx = readFileToString.indexOf("has been modified");
        Assert.assertTrue(idx != -1);
        Assert.assertTrue(readFileToString.indexOf("has been modified", idx + 1) != -1);
    }

    @Test
    public void testMove() throws MojoExecutionException, MojoFailureException {
        copy();

        CopyMojo mojo = new CopyMojo();
        mojo.setShowfiles(true);

        Resource resource = new Resource();
        resource.setId("testMove");
        resource.setMove(true);
        resource.addInclude("copy1/**/*.txt");
        resource.setDirectory(currentLoc);
        resource.addPath(Replace.asReplace("folder2", "copy2/copy3/copy4"));
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertFalse(new File(currentLoc + "/copy1/folder2/test1.txt").exists());
        File file = new File(currentLoc + "/copy1/copy2/copy3/copy4/test1.txt");
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testNothingToDo() throws MojoExecutionException, MojoFailureException {
        CopyMojo mojo = new CopyMojo();
        mojo.setShowfiles(true);

        Resource resource = new Resource();
        resource.setId("testNothingToDo");
        resource.addInclude("folder1/**/*.txt");
        resource.setDirectory(currentLoc);
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertTrue(new File(currentLoc + "/folder1/folder2/test1.txt").exists());
    }

    @Test
    public void testReplaceExisting() throws MojoExecutionException, MojoFailureException {
        copy();

        CopyMojo mojo = new CopyMojo();
        mojo.setShowfiles(true);

        Resource resource = new Resource();
        resource.setId("testMove");
        resource.setMove(false);
        resource.addInclude("copy1/**/*.txt");
        resource.setDirectory(currentLoc);
        resource.addPath(Replace.asReplace("folder2", "copy2"));
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertTrue(new File(currentLoc + "/copy1/folder2/test1.txt").exists());
        File file = new File(currentLoc + "/copy1/copy2/test1.txt");
        Assert.assertTrue(file.exists());

        mojo = new CopyMojo();
        mojo.setShowfiles(true);

        resource = new Resource();
        resource.setId("testMove");
        resource.setMove(true);
        resource.setReplaceExisting(true);
        resource.addInclude("copy1/**/*.txt");
        resource.setDirectory(currentLoc);
        resource.addPath(Replace.asReplace("folder2", "copy2"));
        mojo.setResources(new Resource[] { resource });

        mojo.execute();

        Assert.assertFalse(new File(currentLoc + "/copy1/folder2/test1.txt").exists());
        File file2 = new File(currentLoc + "/copy1/copy2/test1.txt");
        Assert.assertTrue(file2.exists());

        file.delete();
        file2.delete();
    }

    @Test
    public void testReplaceExistingFail() throws MojoExecutionException, MojoFailureException {
        try {
            copy();

            CopyMojo mojo = new CopyMojo();
            mojo.setShowfiles(true);

            Resource resource = new Resource();
            resource.setId("testMove");
            resource.setMove(false);
            resource.addInclude("copy1/**/*.txt");
            resource.setDirectory(currentLoc);
            resource.addPath(Replace.asReplace("folder2", "copy2"));
            mojo.setResources(new Resource[] { resource });

            mojo.execute();

            Assert.assertTrue(new File(currentLoc + "/copy1/folder2/test1.txt").exists());
            File file = new File(currentLoc + "/copy1/copy2/test1.txt");
            Assert.assertTrue(file.exists());

            mojo = new CopyMojo();
            mojo.setShowfiles(true);

            resource = new Resource();
            resource.setId("testMove");
            resource.setMove(true);
            resource.addInclude("copy1/**/*.txt");
            resource.setDirectory(currentLoc);
            resource.addPath(Replace.asReplace("folder2", "copy2"));
            mojo.setResources(new Resource[] { resource });
            mojo.execute();

            Assert.assertFalse("Mojo should have failed 'replaceExisting = false'", true);
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause() instanceof FileExistsException);
        }
    }

    @Test
    public void testSafeWorkingDir() throws MojoExecutionException, MojoFailureException {
        File file = null;
        try {
            CopyMojo mojo = new CopyMojo();
            mojo.setShowfiles(true);

            Resource resource = new Resource();
            resource.setId("testSafeWorkingDir");
            resource.addInclude("folder1/**/*.txt");
            resource.setDirectory(currentLoc);
            resource.addPath(Replace.asReplace(currentLoc, "/tmp"));
            mojo.setResources(new Resource[] { resource });

            mojo.execute();

            file = new File("/tmp/test1.txt");
            Assert.assertFalse(file.exists());
        } finally {
            cleanup(file);
        }
    }

    @Test
    public void testUnsafeWorkingDir() throws MojoExecutionException, MojoFailureException {
        File file = null;
        try {
            CopyMojo mojo = new CopyMojo();

            mojo.setShowfiles(true);

            Resource resource = new Resource();
            resource.setId("testUnsafeWorkingDir");
            resource.setWorkOnFullPath(true);
            resource.addInclude("folder1/**/*.txt");
            resource.setDirectory(currentLoc);
            resource.addPath(Replace.asReplace(currentLoc + "/folder1/folder2", "/tmp")).addPath(
                    Replace.asReplace("test1", "test2"));
            mojo.setResources(new Resource[] { resource });

            mojo.execute();

            file = new File("/tmp/test2.txt");
            Assert.assertTrue(file.exists());
        } finally {
            cleanup(file);
        }
    }
}
