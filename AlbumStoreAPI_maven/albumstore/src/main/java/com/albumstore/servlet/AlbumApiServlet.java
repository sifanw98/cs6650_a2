package com.albumstore.servlet;

import com.albumstore.bean.Album;
import com.albumstore.bean.ErrorMsg;
import com.albumstore.bean.ImageMetaData;
import com.albumstore.util.DatabaseUtil;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/albums/*")
public class AlbumApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check that we have a file upload request
        if (!ServletFileUpload.isMultipartContent(request)) {
            sendErrorResponse(response, "Invalid request. Multipart/form-data content type is expected.");
            return;
        }

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            // Parse the request to get file items.
            List<FileItem> items = upload.parseRequest(request);

            // Process the uploaded items
            Album album = new Album();
            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString();

                    // You should handle all fields from the form
                    switch (fieldName) {
                        case "title":
                            album.setTitle(fieldValue);
                            break;
                        case "artist":
                            album.setArtist(fieldValue);
                            break;
                        case "year":
                            album.setYear(fieldValue);
                            break;
                    }
                } else {
                    // Process form file field (input type="file").
                    String fieldName = item.getFieldName();
                    if ("image".equals(fieldName)) {
                        // Here, we are just getting bytes, but you might want to save to a file instead.
                        album.setImage(item.get());
                    }
                }
            }

            // Validate the parsed album
            StringBuilder errorMessage = new StringBuilder("Invalid request. Missing fields:");

            if (album.getTitle() == null || album.getTitle().trim().isEmpty()) {
                errorMessage.append(" title");
            }

            if (album.getArtist() == null || album.getArtist().trim().isEmpty()) {
                if (errorMessage.length() > 0) {
                    errorMessage.append(",");
                }
                errorMessage.append(" artist");
            }

            if (album.getYear() == null || album.getYear().trim().isEmpty()) {
                if (errorMessage.length() > 0) {
                    errorMessage.append(",");
                }
                errorMessage.append(" year");
            }

            if (album.getImage() == null) {
                if (errorMessage.length() > 0) {
                    errorMessage.append(",");
                }
                errorMessage.append(" image");
            }

// If any field is invalid, send the error response with the detailed message
            if (errorMessage.length() > "Invalid request. Missing fields:".length()) {
                sendErrorResponse(response, errorMessage.toString());
                return;
            }


            // Save album to the database (you will need to modify the saveAlbum method accordingly)
            DatabaseUtil.saveAlbum(album);

            ImageMetaData metaData = new ImageMetaData(String.valueOf(Optional.of(album.getId()).orElse(1)),
                    String.valueOf(album.getImage().length));
            // If you save the image to a file or as a base64 string, you'll need to modify this too.
            sendResponse(response, metaData);

        } catch (Exception ex) {
            sendErrorResponse(response, "Invalid request. Error processing the album data." + ex.getMessage());
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing album ID.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid album ID format.");
            return;
        }

        try {
            Optional<Album> albumOpt = DatabaseUtil.getAlbumByKey(id);

            if (albumOpt.isPresent()) {
                Album album = albumOpt.get();

                sendResponse(response, album);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Album not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error accessing the database.");
        }
    }

    private void sendResponse(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(obj));
    }

    private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrorMsg errorMsg = new ErrorMsg(errorMessage);
        sendResponse(response, errorMsg);
    }
}

