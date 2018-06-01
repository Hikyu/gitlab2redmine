/**
 * @Title: Commit.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.model
 * @author: Yukai  
 * @date: 2018年5月23日 下午4:21:15
 */
package com.oscar.gitlabEventCenter.jpa.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @ClassName: Commit
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月23日 下午4:21:15
 */
@Entity
@Table(
indexes = { 
    @Index(name = "idx_issueID", columnList = "issueID", unique = false)
},
uniqueConstraints = @UniqueConstraint(columnNames={"issueId", "commitUrl", "projectUrl"})
)
public class CommitSet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="commitsetID")
    @SequenceGenerator(name="commitsetID", sequenceName="seq_commitsetID")  
    private long id;
    @Column(nullable=false)
    private long issueId;
    @Column(nullable=false)
    private String commitId;
    @Column(nullable=false)
    private String author;
    @Column(columnDefinition="text")
    private String message;
    @Column(nullable=false)
    private Date timestamp;
    @Column(nullable=false)
    private String commitUrl;
    @Column(nullable=false)
    private String projectName;
    @Column(nullable=false)
    private String projectUrl;
    
    protected CommitSet() {}
    
    private CommitSet(CommitSetBuilder builder) {
        this.author = builder.author;
        this.commitId = builder.commitID;
        this.commitUrl = builder.commitUrl;
        this.issueId = builder.issueID;
        this.message = builder.message;
        this.projectName = builder.projectName;
        this.projectUrl = builder.projectUrl;
        this.timestamp = builder.timestamp;
    }
    
    public long getIssueID() {
        return issueId;
    }

    public String getCommitID() {
        return commitId;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getCommitUrl() {
        return commitUrl;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CommitSet [id=" + id + ", issueId=" + issueId + ", commitId=" + commitId + ", author=" + author
                + ", message=" + message + ", timestamp=" + timestamp + ", commitUrl=" + commitUrl + ", projectName="
                + projectName + ", projectUrl=" + projectUrl + "]";
    }

    public static class CommitSetBuilder {
        private long issueID;
        private String commitID;
        private String author;
        private String message;
        private Date timestamp;
        private String commitUrl;
        private String projectName;
        private String projectUrl;
        
        private CommitSetBuilder() {}
        
        public CommitSetBuilder(long issueID, String commitID) {
            this.issueID = issueID;
            this.commitID = commitID;
            timestamp = new Date();
            message = "";
        }
        
        public CommitSetBuilder author(String author) {
            this.author = author;
            return this;
        }
        
        public CommitSetBuilder commitUrl(String url) {
            this.commitUrl = url;
            return this;
        }
        
        public CommitSetBuilder timestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public CommitSetBuilder projectName(String projectName) {
            this.projectName = projectName;
            return this;
        }
        
        public CommitSetBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        public CommitSetBuilder projectUrl(String projectUrl) {
            this.projectUrl = projectUrl;
            return this;
        }
        
        public CommitSet build() {
            if (this.author == null || this.commitUrl == null || 
                    this.projectName == null || this.author ==null || this.projectUrl == null) {
                throw new IllegalArgumentException("Missing necessary parameters to build commitset.");
            }
            return new CommitSet(this);
        }
        
    }

}
